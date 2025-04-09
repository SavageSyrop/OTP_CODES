package ru.otp.enums;

public enum OtpStatus {
    ACTIVE(1L),
    EXPIRED (2L),
    USED (3L);

    public final Long id;

    OtpStatus(Long id) {
        this.id = id;
    }
}
