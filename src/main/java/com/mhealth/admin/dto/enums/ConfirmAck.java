package com.mhealth.admin.dto.enums;

public enum ConfirmAck {
    NO("0"),
    YES("1");

    private final String value;

    ConfirmAck(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
