package com.tpv.mesas.application.ports;

import com.tpv.mesas.domain.entities.Mesa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MesasPort {
    List<Mesa> obtenerTodas();

    Optional<Mesa> obtenerPorId(UUID id);

    void actualizarMesa(Mesa mesa);
}
