package com.tpv.mesas.domain.entities.vo;

import com.tpv.mesas.domain.entities.ProductoMesa;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class PedidoVO {
    ProductoMesa product;
    String sector;
    Integer numeroMesa;
    String nombreMesa;
    String camarero;
}
