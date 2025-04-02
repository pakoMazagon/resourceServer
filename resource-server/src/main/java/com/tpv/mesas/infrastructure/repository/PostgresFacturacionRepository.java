package com.tpv.mesas.infrastructure.repository;

import com.tpv.mesas.domain.criteria.criteria.Criteria;
import com.tpv.mesas.domain.entities.MesaServida;
import com.tpv.mesas.infrastructure.mappers.MesaServidaRowMapper;
import com.tpv.mesas.infrastructure.persistence.criteria.PostgresCriteriaConverter;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class PostgresFacturacionRepository implements FacturacionRepository {

    private final JdbcTemplate jdbcTemplate;
    private final PostgresCriteriaConverter specificationConverter;
    private final MesaServidaRowMapper mesaServidaRowMapper;

    @Override
    public List<MesaServida> findByCriteria(Criteria criteria) {
        final String queryToExecute = this.specificationConverter.convert(criteria);
        return this.jdbcTemplate.query(queryToExecute, this.mesaServidaRowMapper);
    }
}
