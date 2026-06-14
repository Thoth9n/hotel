package mn.edu.internship.hotel.service;

import mn.edu.internship.hotel.dao.UserDao;
import mn.edu.internship.hotel.model.User;
import mn.edu.internship.hotel.util.PasswordHasher;

import java.sql.SQLException;

public final class AuthService {
    private final UserDao userDao;
    private final PasswordHasher passwordHasher;

    public AuthService(UserDao userDao, PasswordHasher passwordHasher) {
        this.userDao = userDao;
        this.passwordHasher = passwordHasher;
    }

    public User authenticate(String username, String password) {
        String normalizedUsername = username == null ? "" : username.trim();
        if (normalizedUsername.isBlank() || password == null || password.isBlank()) {
            throw new AuthenticationException("Нэвтрэх нэр болон нууц үгээ оруулна уу.");
        }

        try {
            User user = userDao.findActiveByUsername(normalizedUsername)
                    .orElseThrow(() -> new AuthenticationException(
                            "Нэвтрэх нэр эсвэл нууц үг буруу байна."
                    ));

            if (!passwordHasher.matches(password, user.passwordHash())) {
                throw new AuthenticationException("Нэвтрэх нэр эсвэл нууц үг буруу байна.");
            }

            return user;
        } catch (SQLException exception) {
            throw new AuthenticationException(
                    "Өгөгдлийн сантай холбогдож чадсангүй. Тохиргоогоо шалгана уу.",
                    exception
            );
        }
    }
}

