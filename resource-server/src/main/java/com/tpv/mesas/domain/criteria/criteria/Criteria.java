package com.tpv.mesas.domain.criteria.criteria;

import java.util.Objects;
import java.util.Optional;

public class Criteria {
    private final Filters filters;
    private final Order order;
    private final Integer limit;
    private final Integer offset;

    public Criteria(Filters filters, Order order, Integer limit, Integer offset) {
        this.filters = Objects.requireNonNull(filters, "Filters cannot be null");
        this.order = Objects.requireNonNull(order, "Order cannot be null");
        this.limit = limit;
        this.offset = offset;
    }

    public Criteria(Filters filters, Order order) {
        this(filters, order, null, null);
    }

    public boolean hasFilters() {
        return !this.filters.filters().isEmpty();
    }

    // Getters
    public Filters filters() {
        return this.filters;
    }

    public Order order() {
        return this.order;
    }

    public Optional<Integer> limit() {
        return Optional.ofNullable(this.limit);
    }

    public Optional<Integer> offset() {
        return Optional.ofNullable(this.offset);
    }
}
