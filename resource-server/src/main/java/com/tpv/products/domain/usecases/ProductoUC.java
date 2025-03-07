package com.tpv.products.domain.usecases;

import com.tpv.products.domain.entities.Producto;

import java.util.List;

public interface ProductoUC {
    List<Producto> obtenerTodosLosProductos();

    void cambiarEstadoProductoEnMesa(String id, String nuevoEstado);
}
