package ru.otp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.otp.dao.OtpConfigDao;
import ru.otp.entities.OtpConfig;
import ru.otp.entities.UserPrincipal;
import ru.otp.service.UserService;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private OtpConfigDao configDao;

    @Autowired
    private UserService userService;

    private Key secretKey;


    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());
    }

    public String createJWT(Long userId, String username, String role) {
        Date now = new Date();
        OtpConfig otpConfig = configDao.findAll().getFirst();
        Date expiration = new Date(now.getTime() + otpConfig.getExipesAfterMillis());

        Claims claims = Jwts.claims()
                .subject(username)
                .id(userId.toString())
                .add("scope", role)
                .build();
        return jwtProperties.getTokenPrefix() + Jwts.builder().claims(claims)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    @Transactional
    public Authentication getUserAuthorizationFromJWT(String token) throws
            ExpiredJwtException, AuthorizationServiceException {
        token = token.replace(jwtProperties.getTokenPrefix(), "");

        Jws<Claims> claims = Jwts.parser().verifyWith((SecretKey) secretKey)
                .build().parseSignedClaims(token);
        UserPrincipal user = (UserPrincipal) userService.loadUserByUsername(claims.getPayload().getSubject());
        return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
    }
}
