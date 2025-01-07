package com.tpv.products.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Entity
public class Producto {
    @Id
    private UUID id;
    private String code;
    private String nombre;
    private String familia;
    private Double precio1;
    private Double precio2;
    private Double precio3;
    private boolean cocina;
    private String seccion;
}
