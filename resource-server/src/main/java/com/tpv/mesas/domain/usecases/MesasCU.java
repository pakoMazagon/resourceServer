package com.tpv.mesas.domain.usecases;

import com.tpv.mesas.domain.entities.Mesa;

import java.util.List;

public interface MesasCU {
    List<Mesa> obtenerTodas();

    Mesa actualizarMesa(Mesa mesa);
}
