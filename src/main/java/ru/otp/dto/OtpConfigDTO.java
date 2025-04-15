package ru.otp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OtpConfigDTO  {
    private Long otpCodeLength;
    private Long expiresInMillis;
}
