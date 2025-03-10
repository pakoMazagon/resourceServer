package com.tpv.mesas.domain.usecases;

import com.tpv.mesas.domain.entities.MesaServida;

import java.util.List;

public interface MesasCU {
    List<MesaServida> obtenerTodas();

    MesaServida actualizarMesa(MesaServida mesa);

    MesaServida obtenerMesaServidaPorId(String id);

    MesaServida cambiaNombreMesa(String id, String nuevoNombre);

    void eliminar(String id);
}
