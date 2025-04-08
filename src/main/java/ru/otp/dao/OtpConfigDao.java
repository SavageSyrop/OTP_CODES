package ru.otp.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otp.entities.OtpConfig;
import ru.otp.entities.User;

import java.util.Optional;

public interface OtpConfigDao extends JpaRepository<OtpConfig, String> {

    Optional<OtpConfig> findFirst();
}
