package com.tpv.mesas.application.usecases;

import com.tpv.mesas.application.ports.MesaServidaPort;
import com.tpv.mesas.application.ports.MesasPort;
import com.tpv.mesas.application.ports.MesasWSPort;
import com.tpv.mesas.application.ports.ProductoMesaPort;
import com.tpv.mesas.domain.entities.Mesa;
import com.tpv.mesas.domain.entities.MesaServida;
import com.tpv.mesas.domain.entities.ProductoMesa;
import com.tpv.mesas.domain.usecases.MesasCU;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public List<MesaServida> obtenerTodas() {
        final List<MesaServida> listMesasServidasActivas = this.obtenerMesasActivasConProductos();
        final List<Mesa> listMesasMaestras = this.mesaPort.obtenerTodas();


        agregarMesasNoActivas(listMesasMaestras, listMesasServidasActivas);

        // Devolver la lista completa
        return listMesasServidasActivas.stream()
                .sorted(Comparator.comparing(MesaServida::getNumero))
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public MesaServida actualizarMesa(MesaServida mesaServida) {

        final MesaServida mesaServidaBBDD = this.extraerOModificarMesaServida(mesaServida);
        final List<ProductoMesa> productos = Optional.ofNullable(mesaServida.getProducts()).orElse(new ArrayList<>());
        final double cantidadTotal = this.actualizarProductosEnMesa(productos);
        final List<ProductoMesa> productosInBBDD = this.productoMesaPort.findByMesaServidaRef(mesaServidaBBDD.getId().toString());
        //si en la bbdd hay mas productos significa que se ha eliminado un producto y habrá que eliminarlo de la bbdd
        final List<ProductoMesa> productosActualizados = productos.size() < productosInBBDD.size() ? this.eliminarProductoMesa(productos, productosInBBDD) : productosInBBDD;
        mesaServidaBBDD.setProducts(productosActualizados);
        mesaServidaBBDD.setCantidad(cantidadTotal);

        this.mesaServidaPort.update(mesaServidaBBDD);
        this.mesasWSPort.notifyMesaUpdate(mesaServidaBBDD);
        return mesaServidaBBDD;
    }

    private List<ProductoMesa> eliminarProductoMesa(List<ProductoMesa> productos, List<ProductoMesa> productosInBBDD) {
        final List<ProductoMesa> productosCoincidentes = new ArrayList<>();
        final List<UUID> productosIds = productos.stream().map(p -> p.getId()).toList();

        for (final ProductoMesa productoBBDD : productosInBBDD) {
            if (productosIds.contains(productoBBDD.getId())) {
                productosCoincidentes.add(productoBBDD);
            } else {
                this.productoMesaPort.eliminarPorId(productoBBDD.getId());
            }
        }
        return productosCoincidentes;
    }

    private double actualizarProductosEnMesa(List<ProductoMesa> productos) {
        return productos.stream()
                .mapToDouble(producto -> {
                    final double subtotal = producto.getPrecio() * producto.getUnidades();
                    final Optional<ProductoMesa> productoExistente = this.productoMesaPort.findByMesaServidaRefAndProductoRef(
                            producto.getMesaReferencia(), producto.getProductoReferencia());

                    productoExistente.ifPresentOrElse(
                            p -> producto.setId(p.getId()),
                            () -> {
                                producto.setId(null);
                                producto.setFechaHoraCreacion(LocalDateTime.now());
                                producto.setVersion(0);
                            });

                    this.productoMesaPort.createOrUpdate(producto);
                    return subtotal;
                }).sum();
    }

    private MesaServida extraerOModificarMesaServida(MesaServida mesaServida) {
        return Optional.ofNullable(this.mesaServidaPort.obtenerActivaPorMesa(mesaServida.getMesaReferencia()))
                .orElseGet(() -> {
                    final Optional<Mesa> mesaBBDD = this.mesaPort.obtenerPorId(UUID.fromString(mesaServida.getMesaReferencia()));
                    mesaBBDD.ifPresent(m -> {
                        m.completeFromMesaServida(mesaServida);
                        this.mesaPort.actualizarMesa(m);
                    });
                    return mesaBBDD.map(MesaServida::initFromMesa)
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

    private static void agregarMesasNoActivas(List<Mesa> listMesasMaestras, List<MesaServida> listMesasServidasActivas) {
        final Set<String> idsMesasServidas = listMesasServidasActivas.stream()
                .map(MesaServida::getMesaReferencia) // Asumiendo que 'mesaReferencia' es el ID en MesaServida
                .collect(Collectors.toSet());
        listMesasMaestras.stream()
                .filter(mesa -> !idsMesasServidas.contains(mesa.getId().toString())) // Si no está ocupada
                .forEach(mesa -> {
                    final MesaServida nuevaMesaServida = MesaServida.initFromMesa(mesa);
                    nuevaMesaServida.setId(null);
                    nuevaMesaServida.setFechaInicio(null);
                    nuevaMesaServida.setEstado(null);
                    nuevaMesaServida.setActiva(false); // Activa en false si así lo deseas
                    listMesasServidasActivas.add(nuevaMesaServida);
                });
    }

    private List<MesaServida> obtenerMesasActivasConProductos() {
        final List<MesaServida> listMesasServidasActivas = this.mesaServidaPort.obtenerActivas();
        listMesasServidasActivas.forEach(mesaServidaActiva -> {
            mesaServidaActiva.setProducts(this.productoMesaPort.findByMesaServidaRef(mesaServidaActiva.getId().toString()));
        });
        return listMesasServidasActivas;
    }
}
