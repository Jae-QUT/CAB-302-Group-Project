package com.example.mon.app;
import java.sql.SQLException;

public class AuthService {
    public final UserDao userDao = new UserDao();

    //TODO Complete the hashing !!!!!!!
    public boolean login(String username, String password) throws SQLException {
        return userDao.findByUsername(username)
                .map(u -> PasswordUtil.verifyPassword(password, u.getPassword()))
                .orElse(false);
    }

    public boolean register(User newUser) throws SQLException {
        if (newUser.getUsername() == null || newUser.getUsername().isBlank())
            throw new IllegalArgumentException("Username is required.");
        if (userDao.exists(newUser.getUsername()))
            throw new IllegalStateException("Username already exists.");


        String hashed = PasswordUtil.hashPassword(newUser.getPassword());
        newUser.setPassword(hashed);

        return userDao.insert(newUser);
    }

    public boolean register(String u, String pw, String email, int grade) throws SQLException {
        return register(new User(u, pw, email, grade));
    }
}