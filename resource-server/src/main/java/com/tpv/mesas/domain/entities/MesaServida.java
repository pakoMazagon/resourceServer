package com.tpv.mesas.domain.entities;

import com.tpv.mesas.domain.entities.enums.EstadoMesaEnum;
import com.tpv.mesas.domain.entities.enums.MetodoPagoEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Entity
public class MesaServida {
    @Id
    private UUID id; // no podrá tener el mismo id de la mesa, cuidado con el mapper 
    private String mesaReferencia; // id de la mesa
    private int numero; //redundante... Pero voy a usar este Object para proporcionarlo a la capa de infra
    private String sector; //redundante... Pero voy a usar este Object para proporcionarlo a la capa de infra
    private String nombre; // nombre actual o en su defecto nombre tradicional de la mesa 
    private String camarero;
    private Double cantidad;
    private LocalDateTime fechaInicio; // cuando se añade el primer producto
    private LocalDateTime fechaFin; // cuando se cobra
    private Boolean arqueada; // cuando se marque que ha pasado ultimo arqueo
    @Enumerated(EnumType.STRING)
    private EstadoMesaEnum estado;
    @Enumerated(EnumType.STRING)
    private MetodoPagoEnum metodoPago; //cash o tpv
    private Boolean borrada; // si se ha mandado a borrar

    @Transient
    private List<ProductoMesa> products;

    private Boolean ocupada; // marcar ocupada hasta que se cobre. Tan solo puede haber una ocupada por mesa maestra

    @Version
    private int version;
    private LocalDateTime lastUpdatedAt;
    private String lastUpdatedBy;


    public static MesaServida initFromMesa(Mesa mesa, boolean ocupar) {
        return MesaServida.builder()
                .id(UUID.randomUUID())
                .mesaReferencia(mesa.getId().toString())
                .numero(mesa.getNumero())
                .sector(mesa.getSector())
                .nombre(mesa.getNombreActual().isEmpty() ? mesa.getNombreTradicional() : mesa.getNombreActual())
                .camarero(mesa.getCamarero())
                .cantidad(0.0)
                .camarero(ocupar ? mesa.getCamarero() : null)
                .fechaInicio(LocalDateTime.now())
                .arqueada(false)
                .ocupada(ocupar)
                .borrada(false)
                .estado(EstadoMesaEnum.POR_COGER_COMANDA)
                .products(new ArrayList<>())
                .version(1)
                .lastUpdatedBy("InicioMesa")
                .lastUpdatedAt(LocalDateTime.now())
                .build();
    }

    public void borrarMesa() {
        this.setBorrada(true);
        this.setLastUpdatedAt(LocalDateTime.now());
        this.setFechaFin(LocalDateTime.now());
        this.setOcupada(false);
        this.setEstado(EstadoMesaEnum.BORRADA);
    }

    public void liberarMesaServida() {
        this.setOcupada(false); // Ocupada en false si así lo deseas
    }

    public void cobrarMesa(MetodoPagoEnum metodoPago) {
        this.setFechaFin(LocalDateTime.now());
        this.setLastUpdatedAt(LocalDateTime.now());
        this.setEstado(EstadoMesaEnum.COBRADA);
        this.setMetodoPago(metodoPago);
        this.setBorrada(false);
        this.setOcupada(false);
        this.setArqueada(false);
    }

    public void arquearMesa(String camarero) {
        if (!this.estado.equals(EstadoMesaEnum.COBRADA)) {
            this.setFechaFin(LocalDateTime.now());
            this.setMetodoPago(MetodoPagoEnum.POR_ARQUEO);
            this.setLastUpdatedBy(camarero);
        }
        this.setEstado(EstadoMesaEnum.ARQUEADA);
        this.setLastUpdatedAt(LocalDateTime.now());
        this.setBorrada(false);
        this.setOcupada(false);
        this.setArqueada(true);
    }

    public Boolean mesaOcupada() {
        final List<EstadoMesaEnum> estadosNoOcupados = List.of(EstadoMesaEnum.COBRADA, EstadoMesaEnum.ARQUEADA, EstadoMesaEnum.BORRADA);
        return !estadosNoOcupados.contains(this.estado);
    }
}
