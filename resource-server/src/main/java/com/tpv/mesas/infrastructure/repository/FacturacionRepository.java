package com.tpv.mesas.infrastructure.repository;

import com.tpv.mesas.domain.criteria.criteria.Criteria;
import com.tpv.mesas.domain.entities.MesaServida;

import java.util.List;

public interface FacturacionRepository {
    List<MesaServida> findByCriteria(Criteria criteria);
}
