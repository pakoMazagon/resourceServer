package com.tpv.mesas.infrastructure.persistence.criteria;

import com.tpv.mesas.domain.criteria.criteria.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

@Component
public class PostgresCriteriaConverter {
    // Mapeo de operadores de Criteria a PostgreSQL
    private static final Map<FilterOperator.Operator, String> OPERATOR_MAP = Map.of(
            FilterOperator.Operator.EQUAL, "=",
            FilterOperator.Operator.NOT_EQUAL, "<>",
            FilterOperator.Operator.GT, ">",
            FilterOperator.Operator.LT, "<",
            FilterOperator.Operator.GT_OR_EQ, ">=",
            FilterOperator.Operator.LT_OR_EQ, "<=",
            FilterOperator.Operator.CONTAINS, "LIKE",
            FilterOperator.Operator.NOT_CONTAINS, "NOT LIKE",
            FilterOperator.Operator.IN, "IN"
    );

    public String convert(Criteria criteria) {
        final StringBuilder query = new StringBuilder("SELECT * FROM mesa_servida");

        // WHERE clause
        if (criteria.hasFilters()) {
            query.append(" WHERE ").append(this.generateWhereClause(criteria.filters()));
        }

        // ORDER BY clause
        if (criteria.order().hasOrder()) {
            query.append(" ORDER BY ").append(this.generateOrderBy(criteria.order()));
        }

        // LIMIT and OFFSET
        criteria.limit().ifPresent(limit -> query.append(" LIMIT ").append(limit));
        criteria.offset().ifPresent(offset -> query.append(" OFFSET ").append(offset));

        return query.toString();
    }

    private String generateWhereClause(Filters filters) {
        return filters.filters().stream()
                .map(this::convertFilter)
                .reduce((a, b) -> a + " AND " + b)
                .orElse("1=1");
    }

    private String convertFilter(Filter filter) {
        final String operator = OPERATOR_MAP.get(filter.operator().value());
        if (operator == null) {
            throw new IllegalArgumentException("Unexpected operator: " + filter.operator().value());
        }

        final String field = filter.field().value();
        final String value = this.formatValue(filter.value().value(), operator);

        return String.format("%s %s %s", field, operator, value);
    }

    private String formatValue(String value, String operator) {
        if (operator.equals("LIKE") || operator.equals("NOT LIKE")) {
            return "'%" + this.escapeSql(value) + "%'";
        } else if (operator.equals("IN")) {
            return Arrays.stream(value.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(s -> "'" + this.escapeSql(s) + "'")
                    .reduce((a, b) -> a + "," + b)
                    .map(list -> "(" + list + ")")
                    .orElse("('')");
        }
        return "'" + this.escapeSql(value) + "'";
    }

    private String escapeSql(String input) {
        return input.replace("'", "''");
    }

    private String generateOrderBy(Order order) {
        final String field = order.orderBy().value().equals("id") ? "id" : order.orderBy().value();
        final String direction = order.orderType().isAsc() ? "ASC" : "DESC";
        return field + " " + direction;
    }
}
