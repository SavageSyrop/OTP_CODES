package ru.otp.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otp.entities.OtpConfig;

public interface OtpConfigDao extends JpaRepository<OtpConfig, String> {
}
