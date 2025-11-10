package com.tpv.mesas.application.usecases;

import com.tpv.mesas.application.ports.*;
import com.tpv.mesas.domain.entities.Mesa;
import com.tpv.mesas.domain.entities.MesaServida;
import com.tpv.mesas.domain.entities.ProductoMesa;
import com.tpv.mesas.domain.entities.enums.EstadoProductoEnum;
import com.tpv.mesas.domain.entities.enums.MetodoPagoEnum;
import com.tpv.mesas.domain.entities.vo.PedidoVO;
import com.tpv.mesas.domain.usecases.MesasCU;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.print.PrintException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class MesasCUImpl implements MesasCU {

    private final MesasPort mesaPort;

    private final MesaServidaPort mesaServidaPort;

    private final MesasWSPort mesasWSPort;

    private final ProductoMesaPort productoMesaPort;

    private final PedidoWSPort pedidoWSPort;

    private final PrintPort printPort;

    @Override
    public List<MesaServida> obtenerTodas() {
        final List<MesaServida> listMesasServidasOcupadas = this.obtenerMesasOcupadasConProductos();
        final List<Mesa> listMesasMaestras = this.mesaPort.obtenerTodas();


        agregarMesasNoOcupadas(listMesasMaestras, listMesasServidasOcupadas);

        // Devolver la lista completa
        return listMesasServidasOcupadas.stream()
                .sorted(Comparator.comparing(MesaServida::getNumero))
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public MesaServida actualizarMesa(MesaServida mesaServida) {

        final MesaServida mesaServidaBBDD = this.extraerOModificarMesaServida(mesaServida);
        mesaServidaBBDD.setOcupada(true);
        final List<ProductoMesa> productos = Optional.ofNullable(mesaServida.getProducts()).orElse(new ArrayList<>());
        final double cantidadTotal = this.actualizarProductosEnMesa(productos, mesaServida);
        final List<ProductoMesa> productosInBBDD = this.productoMesaPort.findByMesaServidaRef(mesaServidaBBDD.getId().toString());
        //si en la bbdd hay mas productos significa que se ha eliminado un producto y habrá que eliminarlo de la bbdd
        final List<ProductoMesa> productosActualizados = productos.size() < productosInBBDD.size() ? this.eliminarFisicoProductoMesa(productos, productosInBBDD) : productosInBBDD;
        mesaServidaBBDD.setProducts(productosActualizados);
        mesaServidaBBDD.setCantidad(cantidadTotal);

        this.mesaServidaPort.update(mesaServidaBBDD);
        this.mesasWSPort.notifyMesaUpdate(mesaServidaBBDD);
        return mesaServidaBBDD;
    }

    private List<ProductoMesa> eliminarFisicoProductoMesa(List<ProductoMesa> productos, List<ProductoMesa> productosInBBDD) {
        final List<ProductoMesa> productosCoincidentes = new ArrayList<>();
        final List<UUID> productosIds = productos.stream().map(p -> p.getId()).toList();
        //Aqui se borra el pedido.. Creo que no debemos hacer nada

        for (final ProductoMesa productoBBDD : productosInBBDD) {
            if (productosIds.contains(productoBBDD.getId())) {
                productosCoincidentes.add(productoBBDD);
            } else {
                this.productoMesaPort.eliminarPorId(productoBBDD.getId());
            }
        }
        return productosCoincidentes;
    }

    private double actualizarProductosEnMesa(List<ProductoMesa> productos, MesaServida mesaServida) {
        return productos.stream()
                .mapToDouble(producto -> {
                    final double subtotal = producto.getPrecio() * producto.getUnidades();
                    final Optional<ProductoMesa> productoExistente = this.productoMesaPort.findByMesaServidaRefAndProductoRef(
                            producto.getMesaReferencia(), producto.getProductoReferencia());

                    productoExistente.ifPresentOrElse(
                            p -> {
                                producto.setId(p.getId());
                            },
                            () -> {
                                producto.setId(null);
                                producto.setFechaHoraCreacion(LocalDateTime.now());
                                producto.setVersion(0);
                            });
                    final int unidadesPrevias = productoExistente.isPresent() ? productoExistente.get().getUnidades() : 0;
                    this.productoMesaPort.createOrUpdate(producto);
                    if (producto.getVersion() == 0 || unidadesPrevias < producto.getUnidades()) {
                        final PedidoVO productoVO = PedidoVO.builder().product(producto).nombreMesa(mesaServida.getNombre())
                                .camarero(mesaServida.getCamarero()).sector(mesaServida.getSector()).build();
                        if (producto.getEstado() != EstadoProductoEnum.BARRA)
                            this.pedidoWSPort.notifyPedidoUpdate(productoVO);
                    }
                    return subtotal;
                }).sum();
    }

    private MesaServida extraerOModificarMesaServida(MesaServida mesaServida) {
        return Optional.ofNullable(this.mesaServidaPort.obtenerOcupadaPorMesa(mesaServida.getMesaReferencia()))
                .orElseGet(() -> {
                    final Optional<Mesa> mesaBBDD = this.mesaPort.obtenerPorId(UUID.fromString(mesaServida.getMesaReferencia()));
                    mesaBBDD.ifPresent(m -> {
                        m.completeFromMesaServida(mesaServida);
                        this.mesaPort.actualizarMesa(m);
                    });
                    return mesaBBDD.map(m -> MesaServida.initFromMesa(m, true))
                            .map(m -> {
                                this.mesaServidaPort.crearMesaServida(m);
                                return m;
                            })
                            .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));
                });
    }

    @Override
    public MesaServida obtenerMesaServidaPorId(String id) {
        final MesaServida mesaServida = this.mesaServidaPort.obtenerPorId(id);
        if (mesaServida == null) {
            log.error("No existe mesa para el id:{}", id);
        }
        mesaServida.setProducts(this.productoMesaPort.findByMesaServidaRef(id));
        return mesaServida;
    }

    @Override
    @Transactional
    public MesaServida cambiaNombreMesa(String id, String nuevoNombre) {
        final MesaServida mesaServida = this.obtenerMesaServidaPorId(id);
        mesaServida.setNombre(nuevoNombre);
        this.mesaServidaPort.update(mesaServida);
        this.mesasWSPort.notifyMesaUpdate(mesaServida);
        return mesaServida;
    }

    @Override
    @Transactional
    public MesaServida cambiaCamareroMesa(String id, String nuevoCamarero) {
        final MesaServida mesaServida = this.obtenerMesaServidaPorId(id);
        mesaServida.setCamarero(nuevoCamarero);
        this.mesaServidaPort.update(mesaServida);
        this.mesasWSPort.notifyMesaUpdate(mesaServida);
        return mesaServida;
    }

    @Override
    @Transactional
    public void eliminar(String id) {
        final MesaServida mesaServida = this.obtenerMesaServidaPorId(id);
        mesaServida.borrarMesa();
        this.mesaServidaPort.update(mesaServida);
        final Mesa mesaMaestra = this.mesaPort.obtenerPorId(UUID.fromString(mesaServida.getMesaReferencia())).get();
        mesaMaestra.liberar();
        this.mesaPort.actualizarMesa(mesaMaestra);
        final MesaServida mesaServidaDev = MesaServida.initFromMesa(mesaMaestra, false);
        mesaServidaDev.liberarMesaServida();
        this.mesasWSPort.notifyMesaUpdate(mesaServidaDev);
        this.marcarProductosEnMesa(id, EstadoProductoEnum.BORRADO_POR_MESA);
    }

    @Override
    @Transactional
    public void cobrar(String id, MetodoPagoEnum metodoPago) {
        final MesaServida mesaServida = this.obtenerMesaServidaPorId(id);
        mesaServida.cobrarMesa(metodoPago);
        this.mesaServidaPort.update(mesaServida);
        final Mesa mesaMaestra = this.mesaPort.obtenerPorId(UUID.fromString(mesaServida.getMesaReferencia())).get();
        mesaMaestra.liberar();
        this.mesaPort.actualizarMesa(mesaMaestra);
        final MesaServida mesaServidaDev = MesaServida.initFromMesa(mesaMaestra, false);
        this.mesasWSPort.notifyMesaUpdate(mesaServidaDev);
        final List<ProductoMesa> productosMesa = this.productoMesaPort.findByMesaServidaRef(mesaServida.getId().toString());
        productosMesa.forEach(productoMesa -> {
            productoMesa.cobrarProductoMesa();
            this.productoMesaPort.createOrUpdate(productoMesa);
            // en principio parece que no informamos a cocina, cocinatendra pulling cada 2 min
            // si tuviese que actualizar seria aqui... y por este motivo voy de 1 en 1 aunque lo correcto seria marcarlos todos de a 1
        });
    }

    private void marcarProductosEnMesa(String idMesa, EstadoProductoEnum puestoEnMesa) {
        final List<ProductoMesa> productosMesa = this.productoMesaPort.findByMesaServidaRef(idMesa);
        productosMesa.forEach(productoMesa -> {
            productoMesa.setEstado(puestoEnMesa);
            this.productoMesaPort.createOrUpdate(productoMesa);
            // en principio parece que no informamos a cocina, cocinatendra pulling cada 2 min
            // si tuviese que actualizar seria aqui... y por este motivo voy de 1 en 1 aunque lo correcto seria marcarlos todos de a 1
        });
    }

    @Override
    public List<String> obtenerCamareros() {
        return this.mesaServidaPort.obtenerCamareros();
    }

    @Override
    @Transactional
    public void arquearMesas(List<UUID> ids, String usuario) {
        final List<MesaServida> mesasParaArquear = this.mesaServidaPort.obtenerPorIds(ids);
        mesasParaArquear.forEach(mesaServida -> {
            mesaServida.arquearMesa(usuario);
            this.mesaServidaPort.update(mesaServida);
            final List<ProductoMesa> productosMesa = this.productoMesaPort.findByMesaServidaRef(mesaServida.getId().toString());
            productosMesa.forEach(productoMesa -> {
                        productoMesa.arquearProductoMesa();
                        this.productoMesaPort.createOrUpdate(productoMesa);
                    }
            );
        });
    }

    @Override
    public List<PedidoVO> obtenerTodosLosPedidos() {
        final List<ProductoMesa> productosEnCurso = this.productoMesaPort.obtenerTodosEnCurso();
        final Map<String, MesaServida> mesasServidas = new HashMap<>();
        final List<PedidoVO> pedidosList = productosEnCurso.stream().map(productoMesa -> {
            MesaServida mesaServidaRef = null;
            mesaServidaRef = mesasServidas.get(productoMesa.getMesaReferencia());
            if (mesaServidaRef == null) {
                mesaServidaRef = this.mesaServidaPort.obtenerPorId(productoMesa.getMesaReferencia());
            }
            return PedidoVO.builder().product(productoMesa).nombreMesa(mesaServidaRef.getNombre())
                    .camarero(mesaServidaRef.getCamarero()).sector(mesaServidaRef.getSector()).build();
        }).toList();
        return pedidosList;
    }

    @Override
    public void imprimir(String id) throws PrintException {
        final MesaServida mesaServida = this.obtenerMesaServidaPorId(id);
        final List<ProductoMesa> productos = this.productoMesaPort.findByMesaServidaRef(id);
        this.printPort.printTicket("POS-80", mesaServida.getCamarero(), productos, mesaServida);

    }

    private static void agregarMesasNoOcupadas(List<Mesa> listMesasMaestras, List<MesaServida> listMesasServidasOcupadas) {
        final Set<String> idsMesasServidas = listMesasServidasOcupadas.stream()
                .map(MesaServida::getMesaReferencia) // Asumiendo que 'mesaReferencia' es el ID en MesaServida
                .collect(Collectors.toSet());
        listMesasMaestras.stream()
                .filter(mesa -> !idsMesasServidas.contains(mesa.getId().toString())) // Si no está ocupada
                .forEach(mesa -> {
                    final MesaServida nuevaMesaServida = MesaServida.initFromMesa(mesa, false);
                    nuevaMesaServida.liberarMesaServida();
                    listMesasServidasOcupadas.add(nuevaMesaServida);
                });
    }

    private List<MesaServida> obtenerMesasOcupadasConProductos() {
        final List<MesaServida> listMesasServidasOcupadas = this.mesaServidaPort.obtenerOcupadas();
        listMesasServidasOcupadas.forEach(mesaServidaOcupada -> {
            mesaServidaOcupada.setProducts(this.productoMesaPort.findByMesaServidaRef(mesaServidaOcupada.getId().toString()));
            mesaServidaOcupada.setOcupada(true);
        });
        return listMesasServidasOcupadas;
    }


}
