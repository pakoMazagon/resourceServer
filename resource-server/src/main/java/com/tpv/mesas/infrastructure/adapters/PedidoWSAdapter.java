package com.tpv.mesas.infrastructure.adapters;

import com.tpv.mesas.application.ports.PedidoWSPort;
import com.tpv.mesas.domain.entities.vo.PedidoVO;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PedidoWSAdapter implements PedidoWSPort {

    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void notifyPedidoUpdate(PedidoVO pedidoUpdated) {
        this.messagingTemplate.convertAndSend("/topic/pedidos", pedidoUpdated);
    }
}
