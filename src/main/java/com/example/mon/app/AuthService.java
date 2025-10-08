package com.example.mon.app;
import java.sql.SQLException;
import java.util.UUID;

public class AuthService {
    private final UserDao userDao = new UserDao();

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
    public boolean register(String u, String pw, String email, int grade) throws SQLException {
        return register(new User(u, pw, email, grade));
    }

    /// Generating password reset token for user, saves it , returns it///
    public String generateResetToken(String usernameOrEmail){
        try {
            User u = userDao.findByUsernameOrEmail(usernameOrEmail)
                    .orElseThrow(() -> new IllegalArgumentException("No user found with that username or email."));
            String token = UUID.randomUUID().toString();
            long expiry = System.currentTimeMillis() + (15 * 60 * 1000); ///quarter of an hour///

        userDao.saveResetToken(u.getUsername(), token, expiry);
        return token;

        } catch (SQLException e){
            throw new RuntimeException("Error generating reset token: " + e.getMessage());
        }
    }

    public String getEmailForUser(String usernameOrEmail){
        try{
            User u = userDao.findByUsernameOrEmail(usernameOrEmail)
                    .orElseThrow(() ->
                            new IllegalArgumentException("No user found with that username or email."));
            return u.getStudentEmail();
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching email: " + e.getMessage());
        }
    }
    public void resetPassword(String token, String newPassword){
        try {
            User u = userDao.findByResetToken(token)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid or Expired token."));

            long now = System.currentTimeMillis();
            if (u.getResetExpiry() < now) {
                throw new IllegalArgumentException("Reset token has expired.");
            }
            userDao.updatePassword(u.getUsername(), newPassword);
            userDao.clearResetToken(u.getUsername());
        } catch (SQLException e){
            throw new RuntimeException("Database error during reset: " + e.getMessage());
        }
    }

}
