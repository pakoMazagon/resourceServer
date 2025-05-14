package com.tpv.mesas.application.usecases;

import com.tpv.mesas.domain.criteria.criteria.Criteria;
import com.tpv.mesas.domain.criteria.criteria.Filters;
import com.tpv.mesas.domain.criteria.criteria.Order;
import com.tpv.mesas.domain.entities.MesaServida;
import com.tpv.mesas.domain.usecases.FacturacionCU;
import com.tpv.mesas.infrastructure.repository.FacturacionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class FacturacionCUImpl implements FacturacionCU {

    FacturacionRepository facturacionRepository;

    @Override
    public List<MesaServida> findByCriteria(Filters filters, Order order, Integer limit, Integer offset) {
        return this.facturacionRepository.findByCriteria(new Criteria(filters, order, limit, offset));
    }
}
