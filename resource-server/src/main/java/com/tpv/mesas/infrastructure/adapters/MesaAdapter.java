package com.tpv.mesas.infrastructure.adapters;

import com.tpv.mesas.application.ports.MesasPort;
import com.tpv.mesas.domain.entities.Mesa;
import com.tpv.mesas.infrastructure.repository.MesaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class MesaAdapter implements MesasPort {

    private final MesaRepository mesaRepository;

    @Override
    public List<Mesa> obtenerTodas() {
        return this.mesaRepository.findAllByOrderBySectorAscNumeroAsc();
    }

    @Override
    public Optional<Mesa> obtenerPorId(UUID id) {
        return this.mesaRepository.findById(id);
    }

    @Override
    public void actualizarMesa(Mesa mesa) {
        mesa.setLastUpdatedAt(LocalDateTime.now());
        this.mesaRepository.save(mesa);
    }
}
