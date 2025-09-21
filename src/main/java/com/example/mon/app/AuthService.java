package com.example.mon.app;
import java.sql.SQLException;

public class AuthService {
    public final UserDao userDao = new UserDao();

    // DEMO password check (plain text). Replace with hashing in production.
    public boolean login(String username, String password) throws SQLException {
        return userDao.findByUsername(username)
                .map(u -> u.getPassword().equals(password))
                .orElse(false);
    }

    public boolean register(User newUser) throws SQLException {
        if (newUser.getUsername() == null || newUser.getUsername().isBlank())
            throw new IllegalArgumentException("Username is required.");
        if (userDao.exists(newUser.getUsername()))
            throw new IllegalStateException("Username already exists.");
        return userDao.insert(newUser);
    }

    public void register(String u, String pw, String email, int grade) {
    }
}
