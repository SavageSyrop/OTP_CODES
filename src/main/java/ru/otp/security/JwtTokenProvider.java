package ru.otp.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.otp.dto.JwtResponse;
import ru.otp.entities.User;
import ru.otp.entities.UserPrincipal;
import ru.otp.service.UserService;

import javax.crypto.SecretKey;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private UserService userService;

    private Key accessKey;

    private Key refreshKey;


    @PostConstruct
    public void init() {
        this.accessKey = Keys.hmacShaKeyFor(jwtProperties.getAccessSecretKey().getBytes());
        this.refreshKey = Keys.hmacShaKeyFor(jwtProperties.getRefreshSecretKey().getBytes());
    }

    private String createAccessToken(Long userId, String username, String role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.getAccessTokenDuration());

        Claims claims = Jwts.claims()
                .subject(username)
                .id(userId.toString())
                .add("scope", role)
                .build();
        return jwtProperties.getTokenPrefix() + Jwts.builder().claims(claims)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(accessKey)
                .compact();
    }

    private String createRefreshToken(Long userId, String username) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.getRefreshTokenDuration());

        Claims claims = Jwts.claims()
                .subject(username)
                .id(userId.toString())
                .build();
        return jwtProperties.getTokenPrefix() + Jwts.builder().claims(claims)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(refreshKey)
                .compact();
    }

    public Cookie refreshUserTokens(String refreshToken) throws JsonProcessingException,  ExpiredJwtException, AuthorizationServiceException  {
        refreshToken = refreshToken.replace(jwtProperties.getTokenPrefix(), "");
        validateRefreshTokenAuthorization(refreshToken);
        Long userId = Long.valueOf(getUserIdFromRefreshToken(refreshToken));
        User user = userService.getById(userId);

        return createJwtCookie(new UserPrincipal(user));
    }

    private void validateAuthorization(String token, Key key) throws
            ExpiredJwtException, AuthorizationServiceException {
        token = token.replace(jwtProperties.getTokenPrefix(), "");

        Jws<Claims> claims = Jwts.parser().verifyWith((SecretKey) key)
                .build().parseSignedClaims(token);
        UserPrincipal user = (UserPrincipal) userService.loadUserByUsername(claims.getPayload().getSubject());
        userService.validateIfUserCanBeAuthorized(user);
    }

    public void validateAccessTokenAuthorization(String token) throws
            ExpiredJwtException, AuthorizationServiceException {
       validateAuthorization(token, accessKey);
    }

    public void validateRefreshTokenAuthorization(String token) throws
            ExpiredJwtException, AuthorizationServiceException {
        validateAuthorization(token, refreshKey);
    }

    @Transactional
    public Authentication getAuthenticationFromAccessToken(String token) {
        token = token.replace(jwtProperties.getTokenPrefix(), "");
        String username = getUsernameFromToken(token, accessKey);
        UserDetails userDetails = userService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public Cookie createJwtCookie(UserPrincipal user) throws JsonProcessingException {
        JwtResponse jwtResponse = new JwtResponse();
        jwtResponse.setId(user.getUser().getId());
        jwtResponse.setUsername(user.getUsername());
        jwtResponse.setAccessToken(createAccessToken(
                user.getUser().getId(), user.getUsername(), user.getUser().getRole().name())
        );
        jwtResponse.setRefreshToken(createRefreshToken(
                user.getUser().getId(), user.getUsername())
        );
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonVal = objectMapper.writeValueAsString(jwtResponse);
        Cookie cookie = new Cookie(JWTConstants.JWT_COOKIE_NAME.value(), URLEncoder.encode(jsonVal, StandardCharsets.UTF_8));
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", org.springframework.boot.web.server.Cookie.SameSite.NONE.attributeValue());
        cookie.setHttpOnly(true);
        cookie.setDomain("192.168.1.28");      // 192.168.1.28 for local testing, contextcard.ru for hosting
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtProperties.getRefreshTokenDuration()/1000));
        return cookie;
    }

    private String getUserIdFromRefreshToken(String token) {
        token = token.replace(jwtProperties.getTokenPrefix(), "");
        return Jwts.parser()
                .verifyWith((SecretKey) refreshKey)
                .build()
                .parseSignedClaims(token).getPayload().getId();
    }

    private String getUsernameFromToken(String token, Key key) {
        token = token.replace(jwtProperties.getTokenPrefix(), "");
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token).getPayload().getSubject();
    }
}
