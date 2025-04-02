package com.tpv.mesas.domain.criteria.vo;

import java.util.List;

public abstract class EnumValueObject<T> {
    private final T value;
    private final List<T> validValues;

    public EnumValueObject(T value, List<T> validValues) {
        this.validValues = List.copyOf(validValues);
        this.value = value;
        this.checkValueIsValid(value);
    }

    public final T value() {
        return this.value;
    }

    public final List<T> validValues() {
        return this.validValues;
    }

    public final void checkValueIsValid(T value) {
        if (!this.validValues.contains(value)) {
            this.throwErrorForInvalidValue(value);
        }
    }

    protected abstract void throwErrorForInvalidValue(T value);

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[value=" + this.value + "]";
    }
}
