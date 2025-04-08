package ru.otp.service;

import com.google.common.hash.Hashing;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.otp.dao.RoleDao;
import ru.otp.dao.UserDao;
import ru.otp.entities.Role;
import ru.otp.entities.User;
import ru.otp.entities.UserPrincipal;
import ru.otp.enums.RoleType;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private MailService mailService;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<User> user = userDao.findByUsername(username);
        if (user.isEmpty()) {
            throw new EntityNotFoundException(username);
        }
        return new UserPrincipal(user.get());
    }

    @Override
    public User getById(Long id) {
        Optional<User> user = userDao.findById(id);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User " + id + " does not exist");
        }
        return user.get();
    }

    @Override
    @Transactional
    public User saveNewUser(String username, String password) throws Exception {
        User user = new User();
        user.setUsername(username);

        user.setPassword(Hashing.sha256()
                .hashString(password, StandardCharsets.UTF_8)
                .toString());
        user.setActivationCode(UUID.randomUUID().toString());
        user.setIsBanned(false);
        user = userDao.save(user);

        Role role = new Role();
        role.setName(RoleType.USER);
        role.setUserId(user.getId());
        roleDao.save(role);
        mailService.sendActivationEmail(user);
        return user;
    }

    @Override
    public void activateUser(String code) {
        Optional<User> user = userDao.findUserByActivationCode(code);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        User userEntity = user.get();
        userEntity.setActivationCode(null);
        userDao.save(userEntity);
    }

    @Transactional
    @Override
    public User initPasswordReset(String username) throws Exception {
        UserPrincipal userPrincipal = (UserPrincipal) loadUserByUsername(username);
        User user = userPrincipal.getUser();
        user.setResetPasswordCode(UUID.randomUUID().toString());
        user = userDao.save(user);
        mailService.sendActivationEmail(user);
        return user;
    }

    @Override
    public void resetPassword(String code, String newPassword) {
        Optional<User> userOptional = userDao.findByResetPasswordCode(code);
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        User user = userOptional.get();
        user.setPassword(Hashing.sha256()
                .hashString(newPassword, StandardCharsets.UTF_8)
                .toString());
        user.setResetPasswordCode(null);
        userDao.save(user);
    }

    @Override
    public void validateIfUserCanBeAuthorized(UserPrincipal userPrincipal) {
        if (!userPrincipal.isAccountNonLocked()) {
            throw new AuthorizationServiceException("You are banned from this server!");
        }
        if (!userPrincipal.isEnabled()) {
            throw new AuthorizationServiceException("Account is not activated. Check email!");

        }
    }

    @Override
    public User update(User user) {
        return userDao.save(user);
    }
}
