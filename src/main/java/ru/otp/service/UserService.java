package ru.otp.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.otp.entities.User;
import ru.otp.entities.UserPrincipal;

@Service
public interface UserService extends UserDetailsService {

    User getById(Long id);

    User saveNewUser(String username, String password) throws Exception;

    void validateIfUserCanBeAuthorized(UserPrincipal userPrincipal);
}
