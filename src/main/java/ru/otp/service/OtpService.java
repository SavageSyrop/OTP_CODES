package ru.otp.service;

import org.springframework.stereotype.Service;
import ru.otp.entities.OtpConfig;

@Service
public interface OtpService {
    OtpConfig save(OtpConfig newConfig);

    void deleteConfig(OtpConfig oldConfig);

    OtpConfig getConfig();
}
