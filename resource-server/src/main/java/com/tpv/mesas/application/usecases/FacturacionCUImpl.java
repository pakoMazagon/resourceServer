package com.tpv.mesas.application.usecases;

import com.tpv.mesas.application.ports.PrintPort;
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

    PrintPort printPort;

    @Override
    public List<MesaServida> findByCriteria(Filters filters, Order order, Integer limit, Integer offset, Boolean imprime) {
        final List<MesaServida> listaMesas = this.facturacionRepository.findByCriteria(new Criteria(filters, order, limit, offset));
        if (imprime) {
            this.printPort.printArqueo(filters, listaMesas);
        }
        return listaMesas;
    }
}
