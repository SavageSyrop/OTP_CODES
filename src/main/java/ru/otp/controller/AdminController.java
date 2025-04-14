package ru.otp.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.otp.dto.OtpConfigDTO;
import ru.otp.dto.UserDTO;
import ru.otp.entities.OtpConfig;
import ru.otp.entities.User;
import ru.otp.service.OtpService;
import ru.otp.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private ModelMapper modelMapper;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/config}")
    public void editConfig(@RequestBody OtpConfigDTO dto, HttpServletResponse httpServletResponse) {
        log.info("Config change initiated");
        OtpConfig oldConfig = otpService.getConfig();
        OtpConfig newConfig = new OtpConfig();

        if (dto.getOtpCodeLength() == null) {
            newConfig.setOtpCodeLength(oldConfig.getOtpCodeLength());
        } else {
            newConfig.setOtpCodeLength(dto.getOtpCodeLength());
        }
        if (dto.getExipesAfterMillis() == null) {
            newConfig.setOtpCodeLength(oldConfig.getExipesAfterMillis());
        } else {
            newConfig.setOtpCodeLength(dto.getExipesAfterMillis());
        }
        otpService.deleteConfig(oldConfig);
        otpService.saveConfig(newConfig);
        log.info("Config changed");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/getAllUsers}")
    public List<UserDTO> getAllUsers(HttpServletResponse httpServletResponse) {
        List<User> users = userService.getAll();
        List<UserDTO> userDTOS = new ArrayList<>();
        for (User user : users) {
            userDTOS.add(convertToDto(user));
        }
        httpServletResponse.setStatus(200);
        log.info("Requested full user list");
        return userDTOS;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId, HttpServletResponse httpServletResponse) {
        User user = userService.getById(userId);
        userService.deleteById(userId);
        log.info("User with id " + userId + " deleted");
    }

    private UserDTO convertToDto(User user) throws ParseException {
        return modelMapper.map(user, UserDTO.class);
    }
}
