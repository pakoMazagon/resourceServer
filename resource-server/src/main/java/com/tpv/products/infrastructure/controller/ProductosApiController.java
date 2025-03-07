package com.tpv.products.infrastructure.controller;


import com.tpv.products.domain.entities.Producto;
import com.tpv.products.domain.usecases.ProductoUC;
import com.tpv.products.infrastructure.controller.mapper.ProductBBDDMapper;
import com.tpv.products.infrastructure.dto.CambiaEstadoProductoEnMesa200ResponseDTO;
import com.tpv.products.infrastructure.dto.CambiaEstadoProductoEnMesaRequestDTO;
import com.tpv.products.infrastructure.dto.ProductoBBDDDTO;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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

    @Override
    public ResponseEntity<CambiaEstadoProductoEnMesa200ResponseDTO> cambiaEstadoProductoEnMesa(
            @Parameter(name = "id", description = "ID del producto a cambiar el estado", required = true, in = ParameterIn.PATH) @PathVariable("id") String id,
            @Parameter(name = "CambiaEstadoProductoEnMesaRequestDTO", description = "", required = true) @Valid @RequestBody CambiaEstadoProductoEnMesaRequestDTO cambiaEstadoProductoEnMesaRequestDTO
    ) {
        this.productoUC.cambiarEstadoProductoEnMesa(id, cambiaEstadoProductoEnMesaRequestDTO.getNuevoEstado());

        final CambiaEstadoProductoEnMesa200ResponseDTO body = new CambiaEstadoProductoEnMesa200ResponseDTO();
        body.setMessage(String.format("Producto {} modificado a estado {}", id, cambiaEstadoProductoEnMesaRequestDTO.getNuevoEstado()));
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }
}
