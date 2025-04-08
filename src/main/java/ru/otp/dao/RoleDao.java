package ru.otp.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.otp.entities.Role;

@Repository
public interface RoleDao extends JpaRepository<Role, Long> {
}
