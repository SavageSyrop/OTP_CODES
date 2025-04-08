package ru.otp.security;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class JwtProperties {
    @Value("${security.jwt.accessSecretKey}")
    private String accessSecretKey;
    @Value("${security.jwt.refreshSecretKey}")
    private String refreshSecretKey;
    @Value("${security.jwt.accessTokenDuration}")
    private long accessTokenDuration;
    @Value("${security.jwt.refreshTokenDuration}")
    private long refreshTokenDuration;
    @Value("${security.jwt.tokenPrefix}")
    private String tokenPrefix;
}
