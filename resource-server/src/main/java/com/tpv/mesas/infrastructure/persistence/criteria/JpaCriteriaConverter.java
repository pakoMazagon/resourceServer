package com.tpv.mesas.infrastructure.persistence.criteria;

import com.tpv.mesas.domain.criteria.criteria.Criteria;
import com.tpv.mesas.domain.criteria.criteria.Filter;
import com.tpv.mesas.domain.criteria.criteria.Filters;
import com.tpv.mesas.domain.criteria.criteria.Order;
import jakarta.persistence.criteria.*;


public class JpaCriteriaConverter {

    public <T> CriteriaQuery<T> convert(Criteria criteria, CriteriaBuilder cb, Root<T> root) {
        final CriteriaQuery<T> query = (CriteriaQuery<T>) cb.createQuery(root.getJavaType());

        // WHERE
        if (criteria.hasFilters()) {
            query.where(this.buildPredicates(criteria.filters(), cb, root));
        }

        // ORDER BY
        if (criteria.order().hasOrder()) {
            query.orderBy(this.buildOrder(criteria.order(), cb, root));
        }

        return query;
    }

    private <T> Predicate[] buildPredicates(Filters filters, CriteriaBuilder cb, Root<T> root) {
        return filters.filters().stream()
                .map(filter -> this.buildPredicate(filter, cb, root))
                .toArray(Predicate[]::new);
    }

    private <T> Predicate buildPredicate(Filter filter, CriteriaBuilder cb, Root<T> root) {
        final Path<String> field = root.get(filter.field().value());
        final String value = filter.value().value();

        return switch (filter.operator().value()) {
            case EQUAL -> cb.equal(field, value);
            case NOT_EQUAL -> cb.notEqual(field, value);
            case GT -> cb.greaterThan(field, value);
            case LT -> cb.lessThan(field, value);
            case CONTAINS -> cb.like(field, "%" + value + "%");
            case NOT_CONTAINS -> cb.notLike(field, "%" + value + "%");
            default -> throw new IllegalArgumentException("Invalid operator");
        };
    }

    private <T> jakarta.persistence.criteria.Order buildOrder(Order order, CriteriaBuilder cb, Root<T> root) {
        final Path<String> field = root.get(order.orderBy().value());
        return order.orderType().isAsc() ? cb.asc(field) : cb.desc(field);
    }
}