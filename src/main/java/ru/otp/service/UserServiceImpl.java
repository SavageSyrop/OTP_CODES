package ru.otp.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.otp.dao.UserDao;
import ru.otp.entities.User;
import ru.otp.entities.UserPrincipal;
import ru.otp.enums.RoleType;

import java.util.List;
import java.util.Optional;

@Component
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

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
    public void deleteById(Long id) {
        userDao.deleteById(id);
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
    public User saveNewUser(User user) throws Exception {
        userDao.save(user);
        return user;
    }

    @Override
    public boolean adminAlreadyRegistered() {
        Optional<User> admin = userDao.findFirstByRole(RoleType.ADMIN.name());
        return !admin.isEmpty();
    }

    @Override
    public List<User> getAll() {
        return userDao.findAll();
    }
}
