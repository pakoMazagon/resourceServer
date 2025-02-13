package com.tpv.mesas.domain.entities;

import com.tpv.mesas.domain.entities.enums.EstadoMesaEnum;
import com.tpv.mesas.domain.entities.enums.MetodoPagoEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
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
    private Boolean activa; // marcar activa hasta que se cobre. Tan solo puede haber una activa por mesa maestra
    private EstadoMesaEnum estado;
    private MetodoPagoEnum metodoPago; //cash o tpv
    private Boolean borrada; // si se ha mandado a borrar

    @Transient
    private List<ProductoMesa> products;

    @Version
    private int version;
    private LocalDateTime lastUpdatedAt;
    private String lastUpdatedBy;


    public static MesaServida initFromMesa(Mesa mesa) {
        return MesaServida.builder()
                .id(UUID.randomUUID())
                .mesaReferencia(mesa.getId().toString())
                .numero(mesa.getNumero())
                .sector(mesa.getSector())
                .nombre(mesa.getNombreActual().isEmpty() ? mesa.getNombreTradicional() : mesa.getNombreActual())
                .camarero(mesa.getCamarero())
                .cantidad(0.0)
                .camarero(mesa.getCamarero())
                .fechaInicio(LocalDateTime.now())
                .arqueada(false)
                .activa(true)
                .borrada(false)
                .estado(EstadoMesaEnum.POR_COGER_COMANDA)
                .products(new ArrayList<>())
                .version(1)
                .lastUpdatedBy("InicioMesa")
                .lastUpdatedAt(LocalDateTime.now())
                .build();
    }
}
