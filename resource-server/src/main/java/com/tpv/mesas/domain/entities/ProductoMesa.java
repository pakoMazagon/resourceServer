package com.tpv.mesas.domain.entities;

import com.tpv.mesas.domain.entities.enums.EstadoProductoEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
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
public class ProductoMesa {
    @Id
    private UUID id;
    private String mesaReferencia;
    private String prouctoReferencia;
    private Integer unidades;
    private Double precio;
    private String nombre;
    private EstadoProductoEnum estado;
    @Version
    private int version;
}
