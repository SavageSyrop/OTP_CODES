package ru.otp.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.AntPathMatcher;
import ru.otp.dto.JwtResponse;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Slf4j
public class JWTAuthenticationFilter extends BasicAuthenticationFilter {

    private final JwtProperties jwtProperties;

    private final JwtTokenProvider jwtTokenProvider;

    private final Set<String> skipUrls = new HashSet<>(List.of("/api/v1/user/refresh", "/api/v1/user/checkCookie", "/api/v1/user/logout"));

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JwtProperties jwtProperties, JwtTokenProvider jwtTokenProvider) {
        super(authenticationManager);
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtProperties = jwtProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            chain.doFilter(request, response);
            return;
        }
        String cookieValue = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(JWTConstants.JWT_COOKIE_NAME.value()))
                .findFirst()
                .map(Cookie::getValue).orElse(null);

        if (cookieValue == null) {
            chain.doFilter(request, response);
            return;
        }
        String cookieJsonValue = URLDecoder.decode(cookieValue, StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        JwtResponse jwtToken = objectMapper.readValue(cookieJsonValue, JwtResponse.class);
        String accessToken = jwtToken.getAccessToken().replace(jwtProperties.getTokenPrefix(), "");

        jwtTokenProvider.validateAccessTokenAuthorization(accessToken);
        SecurityContextHolder.getContext().setAuthentication(jwtTokenProvider.getAuthenticationFromAccessToken(accessToken));

        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@NotNull HttpServletRequest request) throws ServletException {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return skipUrls.stream().anyMatch(p -> pathMatcher.match(p, request.getRequestURI()));
    }
}
