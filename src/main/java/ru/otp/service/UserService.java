package ru.otp.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.otp.entities.User;

@Service
public interface UserService extends UserDetailsService {

    User getById(Long id);

    User saveNewUser(User user) throws Exception;

    boolean adminAlreadyRegistered();
}
