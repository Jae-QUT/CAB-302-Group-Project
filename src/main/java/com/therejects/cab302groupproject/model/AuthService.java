package com.therejects.cab302groupproject.model;
import com.therejects.cab302groupproject.model.UserDao;

import java.sql.SQLException;
import java.util.UUID;

public class AuthService {
    private final UserDao userDao = new UserDao();

    public String generateResetToken(String usernameOrEmail) {
        try {
            User u = userDao.findByUsernameOrEmail(usernameOrEmail)
                    .orElseThrow(() -> new IllegalArgumentException("No user found with that username or email."));
            String token = UUID.randomUUID().toString();
            long expiry = System.currentTimeMillis() + (15 * 60 * 1000);
            userDao.saveResetToken(u.getUsername(), token, expiry);
            return token;
        } catch (SQLException e) {
            throw new RuntimeException("Error generating reset token: " + e.getMessage());
        }
    }

    public String getEmailForUser(String usernameOrEmail) {
        try {
            User u = userDao.findByUsernameOrEmail(usernameOrEmail)
                    .orElseThrow(() -> new IllegalArgumentException("No user found with that username or email."));
            return u.getStudentEmail();
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching email: " + e.getMessage());
        }
    }

    public void resetPassword(String token, String newPassword) {
        try {
            User u = userDao.findByResetToken(token)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token."));

            long now = System.currentTimeMillis();
            if (u.getResetExpiry() < now) {
                throw new IllegalArgumentException("Reset token has expired.");
            }

            userDao.updatePassword(u.getUsername(), newPassword);
            userDao.clearResetToken(u.getUsername());

        } catch (SQLException e) {
            throw new RuntimeException("Database error during reset: " + e.getMessage());
        }
    }

    public boolean login(String username, String password) throws SQLException {
        var opt = userDao.findByUsername(username);
        if (opt.isEmpty()) {
            return false; // user not found
        }

        User u = opt.get();
        // 🔑 If you’re hashing passwords, replace with PasswordUtil.verifyHash(password, u.getPassword())
        return u.getPassword().equals(password);
    }
    public void register(String username, String password, String email,
                         int gradeYearLevel, int score) throws SQLException {
        if (userDao.exists(username)) {
            throw new IllegalArgumentException("That username already exists.");
        }

        User newUser = new User(username, password, email, gradeYearLevel, score);
        userDao.insert(newUser);
    }
}
