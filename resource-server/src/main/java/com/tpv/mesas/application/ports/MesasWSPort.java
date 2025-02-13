package com.tpv.mesas.application.ports;

import com.tpv.mesas.domain.entities.MesaServida;

public interface MesasWSPort {
    void notifyMesaUpdate(MesaServida mesaUpdated);
}
