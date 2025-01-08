package com.tpv.mesas.infrastructure.adapters;

import com.tpv.mesas.application.ports.MesasPort;
import com.tpv.mesas.domain.entities.Mesa;
import com.tpv.mesas.infrastructure.repository.MesaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class MesaAdapter implements MesasPort {

    private final MesaRepository mesaRepository;

    @Override
    public List<Mesa> obtenerTodas() {
        return this.mesaRepository.findAll();
    }
}
