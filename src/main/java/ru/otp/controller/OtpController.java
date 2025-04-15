package ru.otp.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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
    @GetMapping("/tg")
    public void otpTG(HttpServletResponse httpServletResponse) throws Exception {
        otpService.createOtp(TG);
        httpServletResponse.setStatus(200);
        log.info("TG otp requested");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/phone")
    public void otpPhone(HttpServletResponse httpServletResponse) throws Exception {
        otpService.createOtp(PHONE);
        httpServletResponse.setStatus(200);
        log.info("Phone otp requested");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mail")
    public void otpMail(HttpServletResponse httpServletResponse) throws Exception {
        otpService.createOtp(MAIL);
        httpServletResponse.setStatus(200);
        log.info("Mail otp requested");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/file")
    public void otpFile(HttpServletResponse httpServletResponse) throws Exception {
        otpService.createOtp(FILE);
        httpServletResponse.setStatus(200);
        log.info("File otp requested");
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/tg/validate")
    public void validateTg(@RequestParam String code, HttpServletResponse httpServletResponse) {
        otpService.validate(code, TG);
        httpServletResponse.setStatus(200);
        log.info("Validation success");
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/phone/validate")
    public void validatePhone(@RequestParam String code, HttpServletResponse httpServletResponse) {
        otpService.validate(code, PHONE);
        log.info("Validation success");
        httpServletResponse.setStatus(200);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/mail/validate")
    public void validateMail(@RequestParam String code, HttpServletResponse httpServletResponse) {
        otpService.validate(code, MAIL);
        log.info("Validation success");
        httpServletResponse.setStatus(200);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/file/validate")
    public void validateFile(@RequestParam String code, HttpServletResponse httpServletResponse) {
        otpService.validateFile(code);
        log.info("Validation success");
        httpServletResponse.setStatus(200);
    }
}
