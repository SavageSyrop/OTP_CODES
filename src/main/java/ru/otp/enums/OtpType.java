package ru.otp.enums;

public enum OtpType {
    MAIL(1L),
    PHONE(2L),
    TG(3L),
    FILE(4L);

    public final Long id;

    OtpType(Long id) {
        this.id = id;
    }
}
