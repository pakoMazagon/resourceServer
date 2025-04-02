package com.tpv.mesas.domain.criteria.vo;

public abstract class StringValueObject {

    private final String value;

    public StringValueObject(String value) {
        this.value = value;
    }

    public final String value() {
        return this.value;
    }
}
