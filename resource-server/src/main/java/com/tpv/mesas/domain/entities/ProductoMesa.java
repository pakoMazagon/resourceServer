package com.tpv.mesas.domain.entities;

import com.tpv.mesas.domain.entities.enums.EstadoProductoEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Entity
public class ProductoMesa {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    private String mesaReferencia;
    private String productoReferencia;
    private Integer unidades;
    private Double precio;
    private String nombre;
    private String code;
    @Enumerated(EnumType.STRING)
    private EstadoProductoEnum estado;
    private LocalDateTime fechaHoraCreacion;
    private LocalDateTime fechaHoraPedido;
    private LocalDateTime fechaHoraServido;

    @Version
    private int version;
}
