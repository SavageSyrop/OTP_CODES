package ru.otp.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otp.entities.OtpCode;
import ru.otp.entities.User;
import ru.otp.enums.OtpType;

import java.util.List;

public interface OtpCodesDao extends JpaRepository<OtpCode, Long> {
    List<OtpCode> findAllByOtpTypeAndUser(OtpType otpType, User user);
}
