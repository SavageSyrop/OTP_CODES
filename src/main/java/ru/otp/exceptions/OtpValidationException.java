package ru.otp.exceptions;

public class OtpValidationException extends RuntimeException {
    public OtpValidationException(String message) {
        super(message);
    }
}
