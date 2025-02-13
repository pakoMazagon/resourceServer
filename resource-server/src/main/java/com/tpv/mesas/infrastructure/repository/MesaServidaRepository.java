package com.tpv.mesas.infrastructure.repository;

import com.tpv.mesas.domain.entities.MesaServida;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MesaServidaRepository extends CrudRepository<MesaServida, UUID> {

    Optional<MesaServida> findById(UUID id);

    Optional<MesaServida> findByMesaReferenciaAndActivaTrue(String mesaRefencia);

    List<MesaServida> findByActivaTrue();

}
