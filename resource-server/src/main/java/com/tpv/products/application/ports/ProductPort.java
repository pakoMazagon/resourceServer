package com.tpv.products.application.ports;

import com.tpv.products.domain.entities.Producto;

import java.util.List;

public interface ProductPort {

    List<Producto> findAll();

    List<Producto> findByCocinaTrue();
}
