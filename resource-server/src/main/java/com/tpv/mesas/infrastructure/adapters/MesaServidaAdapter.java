package com.tpv.mesas.infrastructure.adapters;

import com.tpv.mesas.application.ports.MesaServidaPort;
import com.tpv.mesas.domain.entities.MesaServida;
import com.tpv.mesas.infrastructure.repository.MesaServidaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class MesaServidaAdapter implements MesaServidaPort {

    private final MesaServidaRepository mesaServidaRepository;

    @Override
    public List<MesaServida> obtenerOcupadas() {
        return this.mesaServidaRepository.findByOcupadaTrue();
    }

    @Override
    public MesaServida obtenerOcupadaPorMesa(String idMesa) {
        return this.mesaServidaRepository.findByMesaReferenciaAndOcupadaTrue(idMesa).orElse(null);
    }

    @Override
    public void crearMesaServida(MesaServida mesaServida) {
        this.mesaServidaRepository.save(mesaServida);
    }

    @Override
    public MesaServida obtenerPorId(String id) {
        return this.mesaServidaRepository.findById(UUID.fromString(id)).orElse(null);
    }

    @Override
    public List<MesaServida> obtenerPorIds(List<UUID> ids) {
        return (List<MesaServida>) this.mesaServidaRepository.findAllById(ids);
    }

    @Override
    public void update(MesaServida mesaServidaBBDD) {
        this.mesaServidaRepository.save(mesaServidaBBDD);
    }

    @Override
    public List<String> obtenerCamareros() {
        return this.mesaServidaRepository.findDistinctCamareros();
    }
}
