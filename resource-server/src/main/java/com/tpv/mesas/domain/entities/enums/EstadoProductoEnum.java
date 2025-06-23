package com.tpv.mesas.domain.entities.enums;

import java.util.EnumSet;
import java.util.Set;

public enum EstadoProductoEnum {
    BARRA, POR_PEDIR, PEDIDO_A_COCINA, EN_MARCHA_COCINA, SALE_DE_COCINA, BORRADO_POR_MESA, PUESTO_EN_MESA, ARQUEO, COBRADO;

    public static final Set<EstadoProductoEnum> ESTADOS_NO_CAMBIA_COCINA = EnumSet.of(
            BARRA,
            EN_MARCHA_COCINA,
            SALE_DE_COCINA
    );
}
