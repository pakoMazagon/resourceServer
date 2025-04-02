package com.tpv.mesas.domain.criteria.criteria;

import java.util.Objects;
import java.util.Optional;

public final class Order {
    private final OrderBy orderBy;
    private final OrderType orderType;

    public Order(OrderBy orderBy, OrderType orderType) {
        this.orderBy = Objects.requireNonNull(orderBy, "OrderBy cannot be null");
        this.orderType = Objects.requireNonNull(orderType, "OrderType cannot be null");
    }

    //Esto es simplemente otra forma de instanciar nuestra clase
    //La usamos cuando queremos hacer logica extra en nuestra instanciación
    public static Order fromValues(String orderBy, String orderType) {
        if (orderBy == null || orderBy.isBlank()) {
            return new Order(new OrderBy(""), new OrderType(OrderType.OrderTypes.NONE));
        }

        final OrderType type = Optional.ofNullable(orderType)
                .map(OrderType::fromValue)
                .orElse(new OrderType(OrderType.OrderTypes.ASC));

        return new Order(new OrderBy(orderBy), type);
    }

    public boolean hasOrder() {
        return !this.orderType.isNone();
    }

    public OrderBy orderBy() {
        return this.orderBy;
    }

    public OrderType orderType() {
        return this.orderType;
    }

    // Equals, hashCode y toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final Order order = (Order) o;
        return this.orderBy.equals(order.orderBy) && this.orderType.equals(order.orderType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.orderBy, this.orderType);
    }

    @Override
    public String toString() {
        return "Order[orderBy=" + this.orderBy + ", orderType=" + this.orderType + "]";
    }
}
