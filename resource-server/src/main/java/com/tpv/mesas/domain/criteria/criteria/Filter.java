package com.tpv.mesas.domain.criteria.criteria;

import java.util.Map;
import java.util.Objects;

public class Filter {
    private final FilterField field;
    private final FilterOperator operator;
    private final FilterValue value;

    public Filter(FilterField field, FilterOperator operator, FilterValue value) {
        this.field = Objects.requireNonNull(field, "FilterField cannot be null");
        this.operator = Objects.requireNonNull(operator, "FilterOperator cannot be null");
        this.value = Objects.requireNonNull(value, "FilterValue cannot be null");
    }

    // Método de fabricación con validación
    public static Filter fromValues(Map<String, String> values) {
        Objects.requireNonNull(values, "Values map cannot be null");

        final String field = values.get("field");
        final String operator = values.get("operator");
        final String filterValue = values.get("value");

        if (field == null || field.isBlank() ||
                operator == null || operator.isBlank() ||
                filterValue == null || filterValue.isBlank()) {
            throw new IllegalArgumentException("The filter is invalid. Required fields: field, operator, value");
        }

        return new Filter(
                new FilterField(field),
                FilterOperator.fromValue(operator),
                new FilterValue(filterValue)
        );
    }

    // Getters (inmutables)
    public FilterField field() {
        return this.field;
    }

    public FilterOperator operator() {
        return this.operator;
    }

    public FilterValue value() {
        return this.value;
    }

    // Métodos de objeto
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final Filter filter = (Filter) o;
        return this.field.equals(filter.field) &&
                this.operator.equals(filter.operator) &&
                this.value.equals(filter.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.field, this.operator, this.value);
    }

    @Override
    public String toString() {
        return String.format("Filter[field=%s, operator=%s, value=%s]",
                this.field, this.operator, this.value);
    }

    // Método adicional útil para construir queries
//        public String toSqlCondition() {
//            return String.format("%s %s %s",
//                    field.value(),
//                    operator.toSqlOperator(),
//                    value.toSqlValue());
//        }
}
