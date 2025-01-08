package com.tpv.products.infrastructure.adapters;

import com.tpv.products.application.ports.ProductPort;
import com.tpv.products.domain.entities.Producto;
import com.tpv.products.infrastructure.repository.ProductoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class ProductAdapter implements ProductPort {

    private final ProductoRepository productoRepository;

    @Override
    public List<Producto> findAll() {
        return this.productoRepository.findAll();
    }

    @Override
    public List<Producto> findByCocinaTrue() {
        return this.productoRepository.findByCocinaTrue();
    }
}
