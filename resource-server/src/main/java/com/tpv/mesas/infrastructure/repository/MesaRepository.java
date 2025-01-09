package com.tpv.mesas.infrastructure.repository;

import com.tpv.mesas.domain.entities.Mesa;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MesaRepository extends CrudRepository<Mesa, UUID> {

    Optional<Mesa> findById(UUID id);

    List<Mesa> findAllByOrderBySectorAscNumeroAsc();

    List<Mesa> findBySector(String sector);

    List<Mesa> findByCamarero(String camarero);

    List<Mesa> findBySectorAndNumero(String sector, int numero);
}
