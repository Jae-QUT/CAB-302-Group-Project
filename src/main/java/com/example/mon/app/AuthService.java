package com.example.mon.app;
import java.sql.SQLException;

/**
 *
 */
public class AuthService {
    public final UserDao userDao = new UserDao();

    //TODO Complete the hashing !!!!!!!

    /**
     * Takes in User information and compares it against the data stored in the userDao
     *
     * @param username The primary key for the user and identifier for who is currently using the service. Identifier for
     *                 the user to be referenced by other users
     * @param password The chosen password for the user. Uses the {@link PasswordUtil} to unhash the password and confirm.
     * @return whether or not the login infomation matches that of the user based on the bool, lets the user in or asks them to retry
     * @throws SQLException in the case of the SQL not matching the appropriate schema.
     */
    public boolean login(String username, String password) throws SQLException {
        return userDao.findByUsername(username)
                .map(u -> PasswordUtil.verifyPassword(password, u.getPassword()))
                .orElse(false);
    }

    /**
     * After the user register, this inputs the new user information in the login screen for ease of use and a robust experience.
     * @param newUser is the identifier when creating an account and tells the database to add another user
     * @return the {@link UserDao} for the current user and inserts it into the input.
     * @throws SQLException in the case of the SQL not matching the appropriate schema.
     */
    public boolean register(User newUser) throws SQLException {
        if (newUser.getUsername() == null || newUser.getUsername().isBlank())
            throw new IllegalArgumentException("Username is required.");
        if (userDao.exists(newUser.getUsername()))
            throw new IllegalStateException("Username already exists.");


        String hashed = PasswordUtil.hashPassword(newUser.getPassword());
        newUser.setPassword(hashed);

        return userDao.insert(newUser);
    }

    public boolean register(String u, String pw, String email, int grade, int score) throws SQLException {
        return register(new User(u, pw, email, grade, score));
    }
}