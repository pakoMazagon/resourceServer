package com.tpv.products.infrastructure.controller;


import com.tpv.products.domain.entities.Producto;
import com.tpv.products.domain.usecases.ProductoUC;
import com.tpv.products.infrastructure.controller.mapper.ProductBBDDMapper;
import com.tpv.products.infrastructure.dto.ProductoBBDDDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping("")
@RestController
@AllArgsConstructor
public class ProductosApiController implements ProductsApi {

    private ProductoUC productoUC;

    private ProductBBDDMapper mapper;

    @Override
    public ResponseEntity<List<ProductoBBDDDTO>> productsGet() {
        log.info("init REQUEST productsGet");
        final List<Producto> productosList = this.productoUC.obtenerTodosLosProductos();
        return ResponseEntity.status(HttpStatus.OK).body(productosList.stream().map(prod -> this.mapper.mapToProductoBBDDDTO(prod)).toList());
    }
}
