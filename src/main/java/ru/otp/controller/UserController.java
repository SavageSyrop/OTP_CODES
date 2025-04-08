package ru.otp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otp.dto.AuthorizationDTO;
import ru.otp.dto.JwtResponse;
import ru.otp.entities.UserPrincipal;
import ru.otp.security.JwtTokenProvider;
import ru.otp.service.UserService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


@Slf4j
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserService userService;


    @PostAuthorize("returnObject.requiredScope=='PUBLIC' || hasAuthority(returnObject.requiredScope) || returnObject.currentUserIsOwner")
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/login")
    public void login(@RequestBody AuthorizationDTO authorizationDTO, HttpServletResponse httpServletResponse) throws JsonProcessingException {
        UserPrincipal user = (UserPrincipal) userService.loadUserByUsername(authorizationDTO.getUsername());
        userService.validateIfUserCanBeAuthorized(user);
        if (Hashing.sha256()
                .hashString(authorizationDTO.getPassword(), StandardCharsets.UTF_8).toString().equals(user.getUser().getPassword())) {
            Cookie jwtCookie = jwtTokenProvider.createJwtCookie(user);
            httpServletResponse.addCookie(jwtCookie);
        } else {
            throw new AuthorizationServiceException("Invalid credentials");
        }
    }

    @PostMapping("/refresh")
    public void refresh(HttpServletRequest servletRequest, HttpServletResponse httpServletResponse) throws JsonProcessingException {
        Cookie[] cookies = servletRequest.getCookies();
        if (cookies == null) {
            throw new AuthorizationServiceException("Auth cookie is missing! Login again!");
        }
        String cookieValue = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(JWTConstants.JWT_COOKIE_NAME.value()))
                .findFirst()
                .map(Cookie::getValue).orElse(null);

        if (cookieValue == null) {
            throw new AuthorizationServiceException("Auth cookie is null! Login again!");
        }

        String jsonCookieValue = URLDecoder.decode(cookieValue, StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        JwtResponse jwtToken = objectMapper.readValue(jsonCookieValue, JwtResponse.class);

        Cookie jwtCookie = jwtTokenProvider.refreshUserTokens(jwtToken.getRefreshToken());
        httpServletResponse.addCookie(jwtCookie);
    }

    @GetMapping("/checkCookie")
    public boolean checkCookie(HttpServletRequest httpServletRequest) {
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies == null) {
            return false;
        }
        String cookieValue = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(JWTConstants.JWT_COOKIE_NAME.value()))
                .findFirst()
                .map(Cookie::getValue).orElse(null);

        return cookieValue != null;
    }

    @GetMapping("/logout")
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies == null) {
            return;
        }
        Cookie authCookie = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(JWTConstants.JWT_COOKIE_NAME.value()))
                .findFirst().orElse(null);

        if (authCookie != null) {
            authCookie.setMaxAge(0);
            authCookie.setDomain("192.168.1.28");      // 192.168.1.28 for local testing, contextcard.ru for hosting
            authCookie.setAttribute("SameSite", org.springframework.boot.web.server.Cookie.SameSite.NONE.attributeValue());
            authCookie.setSecure(true);
            authCookie.setHttpOnly(true);
            authCookie.setPath("/");
        }
        httpServletResponse.addCookie(authCookie);
    }

    @PostMapping("/register")
    @Transactional
    public void registerNewUser(@RequestBody AuthorizationDTO authorizationDTO) throws Exception {
        userService.saveNewUser(authorizationDTO.getUsername(), authorizationDTO.getPassword());
    }

    @GetMapping("/activateUser/{activationCode}")
    public void activateUser(@PathVariable String activationCode) {
        userService.activateUser(activationCode);
    }

    @PostMapping("/forgotPassword")
    @Transactional
    public void forgotPassword(@RequestBody AuthorizationDTO authorizationDTO) throws Exception {
        userService.initPasswordReset(authorizationDTO.getUsername());
    }

    @PostMapping("/forgotPassword/{codeFromEmail}")
    @Transactional
    public void resetPassword(@RequestBody AuthorizationDTO authorizationDTO, @PathVariable String codeFromEmail) {
        userService.resetPassword(codeFromEmail, authorizationDTO.getPassword());
    }
}
