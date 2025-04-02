package com.tpv.mesas;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.SQLException;

@Testcontainers
public abstract class AbstractPostgresTestContainer {

    @Container
    protected static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:14-alpine")
                    .withDatabaseName("tpv-test")
                    .withUsername("testuser")
                    .withPassword("testpass");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeAll
    static void initDatabase() {
        final Liquibase liquibase;
        try {
            liquibase = createLiquibase();
        } catch (final LiquibaseException e) {
            throw new RuntimeException(e);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            liquibase.update(new Contexts());
        } catch (final LiquibaseException e) {
            throw new RuntimeException("Failed to initialize test database", e);
        }
    }

    private static Liquibase createLiquibase() throws LiquibaseException, SQLException {
        final DatabaseConnection connection = new JdbcConnection(postgreSQLContainer.createConnection(""));
        final ResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor();
        final Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(connection);

        return new Liquibase("test-changelog.xml", resourceAccessor, database);
    }

    @TestConfiguration
    public static class TestDbConfig {
        @Bean
        public DataSource dataSource() {
            return DataSourceBuilder.create()
                    .driverClassName(postgreSQLContainer.getDriverClassName())
                    .url(postgreSQLContainer.getJdbcUrl())
                    .username(postgreSQLContainer.getUsername())
                    .password(postgreSQLContainer.getPassword())
                    .build();
        }

        @Bean
        public JdbcTemplate jdbcTemplate(DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }
    }
}
