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
    private String nombreActual; // tenderia a desaparecer.... El nombre que se le da no es siempre el mismo 
    private boolean ocupada;
    private String camarero; // tenderia a desaparecer.... El camarero no es siempre el mismo
    private Double cantidad; // tenderia a desaparecer.... La cantidad no es siempre la misma
    private LocalDateTime lastUpdatedAt;
    @Version
    private int version;

    public void completeFromMesaServida(MesaServida mesaServida) {
        this.camarero = mesaServida.getCamarero();
        this.nombreActual = mesaServida.getNombre();
        this.ocupada = true;
        this.cantidad = mesaServida.getCantidad();
        this.lastUpdatedAt = LocalDateTime.now();
    }
}
