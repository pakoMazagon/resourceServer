package com.tpv.mesas;

import com.tpv.mesas.application.usecases.FacturacionCUImpl;
import com.tpv.mesas.domain.criteria.criteria.*;
import com.tpv.mesas.domain.entities.MesaServida;
import com.tpv.mesas.domain.usecases.FacturacionCU;
import com.tpv.mesas.infrastructure.mappers.MesaServidaRowMapper;
import com.tpv.mesas.infrastructure.persistence.criteria.PostgresCriteriaConverter;
import com.tpv.mesas.infrastructure.repository.PostgresFacturacionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Testcontainers
@SpringBootTest(classes = {
        PostgresFacturacionRepository.class,
        PostgresCriteriaConverter.class,
        MesaServidaRowMapper.class,
        AbstractPostgresTestContainer.TestDbConfig.class,
        FacturacionCUImpl.class
})
@EnableAutoConfiguration(exclude = {
        LiquibaseAutoConfiguration.class
})
public class PosgressFacturacionRepositoryIT extends AbstractPostgresTestContainer {


    @Autowired
    private PostgresFacturacionRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PostgresCriteriaConverter criteriaConverter;

    @Autowired
    FacturacionCU facturacionCU;// = new FacturacionCUImpl(this.repository);

    @Test
    void findByCriteria_shouldReturnMesasBySector() {
        // Given

        final Filters filters = new Filters(List.of(
                new Filter(
                        new FilterField("sector"),
                        new FilterOperator(FilterOperator.Operator.EQUAL),
                        new FilterValue("Terraza")
                )
        ));

        // When
        final List<MesaServida> result = this.facturacionCU.findByCriteria(filters, Order.fromValues("id", "asc"), null, null, false);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMesaReferencia()).isEqualTo("MESA-01");
    }

    @Test
    void findByCriteria_shouldReturnMesasBySectorAndCamareroAndFechaAndNoBorrada() {
        // Given

        final Filters filters = new Filters(List.of(
                new Filter(
                        new FilterField("sector"),
                        new FilterOperator(FilterOperator.Operator.EQUAL),
                        new FilterValue("terraza")
                ),
                new Filter(
                        new FilterField("camarero"),
                        new FilterOperator(FilterOperator.Operator.EQUAL),
                        new FilterValue("Paco")
                ),
                new Filter(
                        new FilterField("fecha_inicio"),
                        new FilterOperator(FilterOperator.Operator.GT),
                        new FilterValue("2025-05-15T12:15:00")
                ),
                new Filter(
                        new FilterField("fecha_fin"),
                        new FilterOperator(FilterOperator.Operator.LT),
                        new FilterValue("2023-05-15T18:30:00")
                ),
                new Filter(
                        new FilterField("borrada"),
                        new FilterOperator(FilterOperator.Operator.NOT_EQUAL),
                        new FilterValue("false")
                )
        ));

        // When
        final List<MesaServida> result = this.facturacionCU.findByCriteria(filters, Order.fromValues("id", "asc"), null, null, false);

        // Then
        assertThat(result).hasSize(4);
        assertThat(result.get(0).getMesaReferencia()).isEqualTo("MESA-07");
    }

}
