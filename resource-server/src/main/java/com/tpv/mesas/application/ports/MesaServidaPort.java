package com.tpv.mesas.application.ports;

import com.tpv.mesas.domain.entities.MesaServida;

import java.util.List;

public interface MesaServidaPort {
    List<MesaServida> obtenerActivas();

    MesaServida obtenerActivaPorMesa(String idMesa);

    void crearMesaServida(MesaServida mesaServida);

    MesaServida obtenerPorId(String id);

    void update(MesaServida mesaServidaBBDD);
}
