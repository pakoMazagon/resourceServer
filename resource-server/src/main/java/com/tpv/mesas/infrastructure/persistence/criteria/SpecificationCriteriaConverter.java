package com.tpv.mesas.infrastructure.persistence.criteria;

import com.tpv.mesas.domain.criteria.criteria.Criteria;
import com.tpv.mesas.domain.criteria.criteria.Filter;
import com.tpv.mesas.domain.criteria.criteria.Filters;
import org.springframework.data.jpa.domain.Specification;

public class SpecificationCriteriaConverter {

    public <T> Specification<T> convert(Criteria criteria) {
        return (root, query, cb) -> {
            // WHERE
            final Specification<T> spec = criteria.hasFilters()
                    ? this.buildSpecification(criteria.filters())
                    : Specification.where(null);

            // ORDER BY
            if (criteria.order().hasOrder()) {
                query.orderBy(criteria.order().orderType().isAsc()
                        ? cb.asc(root.get(criteria.order().orderBy().value()))
                        : cb.desc(root.get(criteria.order().orderBy().value())));
            }

            return spec.toPredicate(root, query, cb);
        };
    }

    private <T> Specification<T> buildSpecification(Filters filters) {
        return filters.filters().stream()
                .map(this::<T>filterToSpecification)
                .reduce(Specification::and)
                .orElse(null);
    }

    private <T> Specification<T> filterToSpecification(Filter filter) {
        return (root, query, cb) -> {
            switch (filter.operator().value()) {
                case EQUAL:
                    return cb.equal(root.get(filter.field().value()), filter.value().value());
                case NOT_EQUAL:
                    return cb.notEqual(root.get(filter.field().value()), filter.value().value());
                case GT:
                    return cb.greaterThan(root.get(filter.field().value()), filter.value().value());
                case LT:
                    return cb.lessThan(root.get(filter.field().value()), filter.value().value());
                case CONTAINS:
                    return cb.like(root.get(filter.field().value()), "%" + filter.value().value() + "%");
                case NOT_CONTAINS:
                    return cb.notLike(root.get(filter.field().value()), "%" + filter.value().value() + "%");
                case IN:
                    return cb.notLike(root.get(filter.field().value()), "(" + filter.value().value() + ")");
                default:
                    throw new IllegalArgumentException("Invalid operator");
            }
        };
    }
}
