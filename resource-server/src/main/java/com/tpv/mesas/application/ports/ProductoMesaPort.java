package com.tpv.mesas.application.ports;

import com.tpv.mesas.domain.entities.ProductoMesa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductoMesaPort {
    List<ProductoMesa> findByMesaServidaRef(String idMesaServida);

    Optional<ProductoMesa> findByMesaServidaRefAndProductoRef(String idMesaServida, String idProductoRef);

    Optional<ProductoMesa> findById(UUID id);

    ProductoMesa createOrUpdate(ProductoMesa productoMesa);

    void eliminarPorId(UUID id);
}
