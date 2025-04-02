package com.tpv.mesas;

import com.tpv.mesas.domain.criteria.criteria.*;
import com.tpv.mesas.domain.entities.MesaServida;
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
        AbstractPostgresTestContainer.TestDbConfig.class
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


    @Test
    void findByCriteria_shouldReturnMesasBySector() {
        // Given
        final Criteria criteria = new Criteria(
                new Filters(List.of(
                        new Filter(
                                new FilterField("sector"),
                                new FilterOperator(FilterOperator.Operator.EQUAL),
                                new FilterValue("TERRAZA")
                        )
                )),
                Order.fromValues("id", "asc"),
                100,
                0
        );

        // When
        final List<MesaServida> result = this.repository.findByCriteria(criteria);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMesaReferencia()).isEqualTo("MESA-01");
    }

}
