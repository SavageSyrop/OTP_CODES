package ru.otp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.otp.entities.User;

@Component
@Slf4j
public class TgService {
    public void sendOtpMessage(User currentUser, String otpCode) {
    }
}
