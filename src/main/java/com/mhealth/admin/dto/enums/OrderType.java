package com.mhealth.admin.dto.enums;

public enum OrderType {
    ONE("1"), ZERO("0");

    private final String value;

    OrderType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
