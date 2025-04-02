package com.tpv.products.application.usecases;

import com.tpv.mesas.application.ports.MesaServidaPort;
import com.tpv.mesas.application.ports.MesasWSPort;
import com.tpv.mesas.application.ports.ProductoMesaPort;
import com.tpv.mesas.domain.entities.MesaServida;
import com.tpv.mesas.domain.entities.ProductoMesa;
import com.tpv.mesas.domain.entities.enums.EstadoProductoEnum;
import com.tpv.products.application.ports.ProductPort;
import com.tpv.products.domain.entities.Producto;
import com.tpv.products.domain.usecases.ProductoUC;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class ProductoUCImpl implements ProductoUC {

    private final ProductPort productPort;

    private final MesaServidaPort mesaServidaPort;

    private final MesasWSPort mesasWSPort;

    private final ProductoMesaPort productoMesaPort;

    @Override
    public List<Producto> obtenerTodosLosProductos() {
        return this.productPort.findAll();
    }

    @Override
    public void cambiarEstadoProductoEnMesa(String id, String nuevoEstado) {

        final Optional<ProductoMesa> prodMesaOpt = this.productoMesaPort.findById(UUID.fromString(id));
        if (prodMesaOpt.isEmpty()) {
            log.error("No hay producto on ese id" + id);
            throw new RuntimeException("Producto inexistente");
        }

        final ProductoMesa prodMesa = prodMesaOpt.get();
        prodMesa.setEstado(EstadoProductoEnum.valueOf(nuevoEstado));
        this.productoMesaPort.createOrUpdate(prodMesa);
        //aqui deberia ir al WS de cocina
        final MesaServida mesaServida = this.mesaServidaPort.obtenerPorId(prodMesa.getMesaReferencia());
        mesaServida.setOcupada(true);
        mesaServida.setProducts(this.productoMesaPort.findByMesaServidaRef(prodMesa.getMesaReferencia()));
        this.mesasWSPort.notifyMesaUpdate(mesaServida);
    }
}
