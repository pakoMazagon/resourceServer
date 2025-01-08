package com.tpv.mesas.domain.entities;

import com.tpv.mesas.domain.entities.enums.EstadoMesaEnum;
import com.tpv.mesas.domain.entities.enums.MetodoPagoEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
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
public class MesaServida {
    @Id
    private UUID id;
    private String mesaReferencia;
    private String nombre;
    private String camarero;
    private Double cantidad;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Boolean arqueada;
    private Boolean activa;
    private EstadoMesaEnum estado;
    private MetodoPagoEnum metodoPago;
    @Version
    private int version;
    private LocalDateTime lastUpdatedAt;
    private String lastUpdatedBy;
}
