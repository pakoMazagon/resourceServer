package com.tpv.mesas.infrastructure.repository;

import com.tpv.mesas.domain.entities.MesaServida;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MesaServidaRepository extends CrudRepository<MesaServida, UUID> {

    Optional<MesaServida> findById(UUID id);

    Optional<MesaServida> findByMesaReferenciaAndOcupadaTrue(String mesaRefencia);

    List<MesaServida> findByOcupadaTrue();

    @Query("SELECT DISTINCT m.camarero FROM MesaServida m WHERE m.camarero IS NOT NULL AND m.camarero <> ''")
    List<String> findDistinctCamareros();

}
