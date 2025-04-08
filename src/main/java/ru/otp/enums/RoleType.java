package ru.otp.enums;

public enum RoleType {
    ADMIN(1L),
    USER(2L);

    public final Long id;

    RoleType(Long id) {
        this.id = id;
    }
}
