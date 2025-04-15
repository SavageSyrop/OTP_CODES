package ru.otp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.otp.enums.RoleType;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationDTO {
    @Email
    private String email;
    private String password;
    private String phoneNumber;
    private String tgId;
    private RoleType roleType;
}
