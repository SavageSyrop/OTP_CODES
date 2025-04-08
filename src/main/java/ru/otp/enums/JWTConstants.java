package ru.otp.enums;

public enum JWTConstants {
    JWT_COOKIE_NAME("bcard_jwt_auth");

    private final String value;
    JWTConstants(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
