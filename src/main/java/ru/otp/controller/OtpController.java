package ru.otp.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.otp.service.OtpService;
import ru.otp.service.UserService;

import static ru.otp.enums.OtpType.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/otp")
public class OtpController {

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private ModelMapper modelMapper;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/tg")
    public void otpTG(HttpServletResponse httpServletResponse) throws Exception {
        otpService.createOtp(TG);
        httpServletResponse.setStatus(200);
        log.info("TG otp requested");
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/phone")
    public void otpPhone(HttpServletResponse httpServletResponse) throws Exception {
        otpService.createOtp(PHONE);
        httpServletResponse.setStatus(200);
        log.info("Phone otp requested");
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/mail")
    public void otpMail(HttpServletResponse httpServletResponse) throws Exception {
        otpService.createOtp(MAIL);
        httpServletResponse.setStatus(200);
        log.info("Mail otp requested");
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/file")
    public void otpFile(HttpServletResponse httpServletResponse) throws Exception {
        otpService.createOtp(FILE);
        httpServletResponse.setStatus(200);
        log.info("File otp requested");
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/validate/tg")
    public void validateTg(@RequestParam String code, HttpServletResponse httpServletResponse) {
        if (otpService.validate(code, TG)) {
            httpServletResponse.setStatus(200);
            log.info("Validation success");
        } else {
            log.info("Validation error");
            httpServletResponse.setStatus(500);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/validate/phone")
    public void validatePhone(@RequestParam String code, HttpServletResponse httpServletResponse) {
        if (otpService.validate(code, PHONE)) {
            log.info("Validation success");
            httpServletResponse.setStatus(200);
        } else {
            log.info("Validation error");
            httpServletResponse.setStatus(500);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/validate/mail")
    public void validateMail(@RequestParam String code, HttpServletResponse httpServletResponse) {
        if (otpService.validate(code, MAIL)) {
            log.info("Validation success");
            httpServletResponse.setStatus(200);
        } else {
            log.info("Validation error");
            httpServletResponse.setStatus(500);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/validate/file")
    public void validateFile(@RequestParam String code, HttpServletResponse httpServletResponse) {
        if (otpService.validateFile(code)) {
            log.info("Validation success");
            httpServletResponse.setStatus(200);
        } else {
            log.info("Validation error");
            httpServletResponse.setStatus(500);
        }
    }
}
