package ru.otp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.hash.Hashing;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otp.dto.AuthorizationDTO;
import ru.otp.entities.User;
import ru.otp.entities.UserPrincipal;
import ru.otp.enums.RoleType;
import ru.otp.security.JwtTokenProvider;
import ru.otp.service.UserService;

import java.nio.charset.StandardCharsets;


@Slf4j
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserService userService;

//    @PostAuthorize("returnObject.requiredScope=='PUBLIC' || hasAuthority(returnObject.requiredScope) || returnObject.currentUserIsOwner")
//    @PreAuthorize("hasAuthority('USER')")

    @PostMapping("/login")
    public void login(@RequestBody AuthorizationDTO authorizationDTO,HttpServletResponse httpServletResponse) throws JsonProcessingException {
        UserPrincipal user = (UserPrincipal) userService.loadUserByUsername(authorizationDTO.getEmail());
        if (Hashing.sha256()
                .hashString(authorizationDTO.getPassword(), StandardCharsets.UTF_8).toString().equals(user.getUser().getPassword())) {
            String jwtValue = jwtTokenProvider.createJWT(user.getUser().getId(), user.getUsername(), user.getPassword());
            httpServletResponse.setHeader(HttpHeaders.AUTHORIZATION, jwtValue);
        } else {
            throw new AuthorizationServiceException("Invalid credentials");
        }
    }

    @PostMapping("/register")
    @Transactional
    public void registerNewUser(@RequestBody AuthorizationDTO authorizationDTO) throws Exception {
        if (RoleType.ADMIN.name().equals(authorizationDTO.getRoleType()) && userService.adminAlreadyRegistered()) {
            throw new IllegalArgumentException("Admin already exists");
        }
        User user = new User();
        user.setEmail(authorizationDTO.getEmail());
        user.setRole(RoleType.valueOf(authorizationDTO.getRoleType()));
        user.setPhoneNumber(authorizationDTO.getPhoneNumber());
        user.setTgId(authorizationDTO.getTgId());
        user.setPassword(Hashing.sha256()
                .hashString(authorizationDTO.getPassword(), StandardCharsets.UTF_8)
                .toString());
        userService.saveNewUser(user);
    }
}
