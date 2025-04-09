package ru.otp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationDTO {
    @Email
    private String email;
    @Size(min = 8, message = "password should have at least 8 characters")
    private String password;
    private String phoneNumber;
    private String tgId;
    private String roleType;
}
