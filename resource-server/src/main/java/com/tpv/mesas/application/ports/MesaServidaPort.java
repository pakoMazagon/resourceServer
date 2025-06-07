package com.tpv.mesas.application.ports;

import com.tpv.mesas.domain.entities.MesaServida;

import java.util.List;
import java.util.UUID;

public interface MesaServidaPort {
    List<MesaServida> obtenerOcupadas();

    MesaServida obtenerOcupadaPorMesa(String idMesa);

    void crearMesaServida(MesaServida mesaServida);

    MesaServida obtenerPorId(String id);

    List<MesaServida> obtenerPorIds(List<UUID> ids);

    void update(MesaServida mesaServidaBBDD);

    List<String> obtenerCamareros();
}
