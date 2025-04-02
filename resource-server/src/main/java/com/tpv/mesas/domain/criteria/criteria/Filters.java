package com.tpv.mesas.domain.criteria.criteria;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Filters {
    private final List<Filter> filters;

    public Filters(List<Filter> filters) {
        this.filters = Collections.unmodifiableList(
                Objects.requireNonNull(filters, "Filters list cannot be null")
        );
    }

    // Método de fabricación desde lista de mapas
    public static Filters fromValues(List<Map<String, String>> filterMaps) {
        Objects.requireNonNull(filterMaps, "Filter maps list cannot be null");

        return new Filters(
                filterMaps.stream()
                        .map(Filter::fromValues)
                        .collect(Collectors.toList())
        );
    }

    public static Filters none() {
        return new Filters(List.of());
    }

    public List<Filter> filters() {
        return this.filters;
    }

    public boolean isEmpty() {
        return this.filters.isEmpty();
    }
    

    // Métodos de objeto
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final Filters filters1 = (Filters) o;
        return this.filters.equals(filters1.filters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.filters);
    }

    @Override
    public String toString() {
        return "Filters{filters=" + this.filters + "}";
    }
}
