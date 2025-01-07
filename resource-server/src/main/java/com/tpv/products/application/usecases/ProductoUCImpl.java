package com.tpv.products.application.usecases;

import com.tpv.products.domain.entities.Producto;
import com.tpv.products.domain.usecases.ProductoUC;
import com.tpv.products.infrastructure.repository.ProductoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ProductoUCImpl implements ProductoUC {

    private final ProductoRepository productoRepository;

    @Override
    public List<Producto> obtenerTodosLosProductos() {
        return this.productoRepository.findAll();
    }
}
