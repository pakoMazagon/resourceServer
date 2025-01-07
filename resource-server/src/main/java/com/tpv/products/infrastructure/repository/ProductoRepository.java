package com.tpv.products.infrastructure.repository;

import com.tpv.products.domain.entities.Producto;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface ProductoRepository extends CrudRepository<Producto, UUID> {
    List<Producto> findAll();

    List<Producto> findByCocinaTrue();
}
