package com.tpv.mesas.application.ports;

import com.tpv.mesas.domain.entities.Mesa;

import java.util.List;

public interface MesasPort {
    List<Mesa> obtenerTodas();

    void actualizarMesa(Mesa mesa);
}
