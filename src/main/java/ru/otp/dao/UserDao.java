package ru.otp.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otp.entities.User;

import java.util.Optional;

public interface UserDao extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findFirstByRole(String role);
}
