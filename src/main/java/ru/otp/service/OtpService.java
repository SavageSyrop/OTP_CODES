package ru.otp.service;

import org.springframework.stereotype.Service;
import ru.otp.entities.OtpConfig;
import ru.otp.enums.OtpType;

@Service
public interface OtpService {
    OtpConfig saveConfig(OtpConfig newConfig);

    OtpConfig getConfig();

    void validate(String code, OtpType otpType);

    void createOtp(OtpType otpType) throws Exception;

    void validateFile(String code);
}

