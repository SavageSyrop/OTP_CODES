package ru.otp.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otp.entities.OtpCodes;
import ru.otp.entities.User;

import java.util.Optional;

public interface OtpCodesDao extends JpaRepository<OtpCodes, Long> {
}
