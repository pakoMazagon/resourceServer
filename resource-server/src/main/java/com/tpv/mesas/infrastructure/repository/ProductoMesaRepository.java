package com.tpv.mesas.infrastructure.repository;

import com.tpv.mesas.domain.entities.ProductoMesa;
import com.tpv.mesas.domain.entities.enums.EstadoProductoEnum;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductoMesaRepository extends CrudRepository<ProductoMesa, UUID> {
    List<ProductoMesa> findByMesaReferenciaOrderByFechaHoraCreacion(String mesaRef);

    Optional<ProductoMesa> findByMesaReferenciaAndProductoReferencia(String mesaRef, String productRef);

    List<ProductoMesa> findAllByEstadoIn(List<EstadoProductoEnum> statuses);
}
