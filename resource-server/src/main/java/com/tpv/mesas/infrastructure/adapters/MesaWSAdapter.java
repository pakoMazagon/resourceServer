package com.tpv.mesas.infrastructure.adapters;

import com.tpv.mesas.application.ports.MesasWSPort;
import com.tpv.mesas.domain.entities.MesaServida;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MesaWSAdapter implements MesasWSPort {

    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void notifyMesaUpdate(MesaServida mesaUpdated) {
        this.messagingTemplate.convertAndSend("/topic/mesas", mesaUpdated);
    }
}
