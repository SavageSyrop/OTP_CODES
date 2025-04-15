package ru.otp.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otp.entities.User;
import ru.otp.enums.RoleType;

import java.util.Optional;

public interface UserDao extends JpaRepository<User, Long> {

    Optional<User> findFirstByRole(RoleType role);

    Optional<User> findByEmail(String email);
}
