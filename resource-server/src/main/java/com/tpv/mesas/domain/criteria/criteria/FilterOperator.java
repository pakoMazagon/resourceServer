package com.tpv.mesas.domain.criteria.criteria;

import com.tpv.mesas.domain.criteria.vo.EnumValueObject;

import java.util.Arrays;

public class FilterOperator extends EnumValueObject<FilterOperator.Operator> {
    public enum Operator {
        EQUAL("="),
        NOT_EQUAL("!="),
        GT(">"),
        LT("<"),
        CONTAINS("CONTAINS"),
        NOT_CONTAINS("NOT_CONTAINS");

        private final String symbol;

        Operator(String symbol) {
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            return this.symbol;
        }
    }

    public FilterOperator(Operator value) {
        super(value, Arrays.asList(Operator.values()));
    }

    public static FilterOperator fromValue(String value) {
        return Arrays.stream(Operator.values())
                .filter(op -> op.toString().equals(value))
                .findFirst()
                .map(FilterOperator::new)
                .orElseThrow(() -> new IllegalArgumentException(
                        "The filter operator " + value + " is invalid. " +
                                "Valid operators are: " + Arrays.toString(Operator.values()))
                );
    }

    public boolean isPositive() {
        return this.value() != Operator.NOT_EQUAL && this.value() != Operator.NOT_CONTAINS;
    }

    @Override
    protected void throwErrorForInvalidValue(Operator value) {
        throw new IllegalArgumentException(
                "The filter operator " + value + " is invalid. " +
                        "Valid operators are: " + this.validValues()
        );
    }

    public static FilterOperator equal() {
        return new FilterOperator(Operator.EQUAL);
    }

    // Método adicional útil para Java
    public String toSqlOperator() {
        return switch (this.value()) {
            case CONTAINS, NOT_CONTAINS -> "LIKE";
            case EQUAL -> "=";
            case NOT_EQUAL -> "<>";
            case GT -> ">";
            case LT -> "<";
        };
    }
}
