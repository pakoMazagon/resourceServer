package com.tpv.mesas.infrastructure.adapters;

import com.tpv.mesas.application.ports.ProductoMesaPort;
import com.tpv.mesas.domain.entities.ProductoMesa;
import com.tpv.mesas.domain.entities.enums.EstadoProductoEnum;
import com.tpv.mesas.infrastructure.repository.ProductoMesaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class ProductoMesaAdapter implements ProductoMesaPort {

    private final ProductoMesaRepository productoMesaRepository;

    @Override
    public List<ProductoMesa> findByMesaServidaRef(String idMesaServida) {
        return this.productoMesaRepository.findByMesaReferenciaOrderByFechaHoraCreacion(idMesaServida);
    }

    @Override
    public Optional<ProductoMesa> findByMesaServidaRefAndProductoRef(String idMesaServida, String idProductoRef) {
        return this.productoMesaRepository.findByMesaReferenciaAndProductoReferencia(idMesaServida, idProductoRef);
    }

    @Override
    public Optional<ProductoMesa> findById(UUID id) {
        return this.productoMesaRepository.findById(id);
    }

    @Override
    public ProductoMesa createOrUpdate(ProductoMesa productoMesa) {
        return this.productoMesaRepository.save(productoMesa);
    }

    @Override
    public void eliminarPorId(UUID id) {
        this.productoMesaRepository.deleteById(id);
    }

    @Override
    public List<ProductoMesa> obtenerTodosEnCurso() {
        return this.productoMesaRepository.findAllByEstadoIn(Arrays.asList(EstadoProductoEnum.PEDIDO_A_COCINA, EstadoProductoEnum.POR_PEDIR, EstadoProductoEnum.EN_MARCHA_COCINA));
    }
}
