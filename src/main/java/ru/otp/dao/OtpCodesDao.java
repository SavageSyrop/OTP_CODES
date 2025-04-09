package ru.otp.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otp.entities.OtpCodes;

public interface OtpCodesDao extends JpaRepository<OtpCodes, Long> {
}
