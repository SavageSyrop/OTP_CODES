package ru.otp.security;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class JwtProperties {
    @Value("${security.jwt.secretKey}")
    private String secretKey;
    @Value("${security.jwt.tokenPrefix}")
    private String tokenPrefix;
    @Value("${security.jwt.tokenDuration}")
    private String tokenDuration;
}
