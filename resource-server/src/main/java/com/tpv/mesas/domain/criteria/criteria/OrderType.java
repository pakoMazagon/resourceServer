package com.tpv.mesas.domain.criteria.criteria;

import com.tpv.mesas.domain.criteria.vo.EnumValueObject;

import java.util.Arrays;
import java.util.List;

public final class OrderType extends EnumValueObject {

    public enum OrderTypes {
        ASC("asc"),
        DESC("desc"),
        NONE("none");

        private final String value;

        OrderTypes(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    public OrderType(OrderTypes value) {
        super(value, List.of(OrderTypes.values()));
    }

    //Esto es simplemente otra forma de instanciar nuestra clase
    //La usamos cuando queremos hacer logica extra en nuestra instanciación
    //En este caso nosotro queremos evaluar que el campo value sea del tipo enum OrderTypes
    public static OrderType fromValue(String value) {
        final OrderTypes enumValue = Arrays.stream(OrderTypes.values())
                .filter(orderType -> orderType.toString().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "The order type " + value + " is invalid. " +
                                "Valid values are: " + Arrays.toString(OrderTypes.values())
                ));

        return new OrderType(enumValue);
    }

    public boolean isNone() {
        return this.value() == OrderTypes.NONE;
    }

    public boolean isAsc() {
        return this.value() == OrderTypes.ASC;
    }


    @Override
    protected void throwErrorForInvalidValue(Object value) {
        throw new IllegalArgumentException(
                "The order type " + value + " is invalid. " +
                        "Valid values are: " + this.validValues()
        );
    }
}
