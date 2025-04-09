package ru.otp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;


@Slf4j
public class JWTAuthenticationFilter extends BasicAuthenticationFilter {

    private final JwtProperties jwtProperties;

    private final JwtTokenProvider jwtTokenProvider;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JwtProperties jwtProperties, JwtTokenProvider jwtTokenProvider) {
        super(authenticationManager);
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtProperties = jwtProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null) {
            chain.doFilter(request, response);
            return;
        }

        String accessToken = authHeader.replace(jwtProperties.getTokenPrefix(), "");

        Authentication auth = jwtTokenProvider.getUserAuthorizationFromJWT(accessToken);
        SecurityContextHolder.getContext().setAuthentication(auth);

        chain.doFilter(request, response);
    }
}
