package com.tpv.mesas.application.ports;

import com.tpv.mesas.domain.entities.vo.PedidoVO;

public interface PedidoWSPort {
    void notifyPedidoUpdate(PedidoVO pedidoUpdated);
}
