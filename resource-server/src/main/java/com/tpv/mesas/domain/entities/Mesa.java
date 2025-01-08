package com.tpv.mesas.domain.entities;

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
public class Mesa {
    @Id
    private UUID id;
    private int numero;
    private String sector;
    private String nombreTradicional;
    private String nombreActual;
    private boolean ocupada;
    private String camarero;
    private Double cantidad;
    private LocalDateTime lastUpdatedAt;
    @Version
    private int version;
}
