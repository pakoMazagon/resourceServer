package com.tpv.mesas.domain.usecases;

import com.tpv.mesas.domain.criteria.criteria.Filters;
import com.tpv.mesas.domain.criteria.criteria.Order;
import com.tpv.mesas.domain.entities.MesaServida;

import java.util.List;

public interface FacturacionCU {
    List<MesaServida> findByCriteria(Filters filters, Order order, Integer limit, Integer offset);
}
