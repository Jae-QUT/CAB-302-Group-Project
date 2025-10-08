package com.example.mon.app;

import java.sql.*;
import java.util.Optional;




/**
 * Data Access Object (DAO) for {@link User} entities.
 * <p>
 * This class provides CRUD-like operations for interacting with the
 * {@code LoginRegisterUI} table in the authentication database.
 * It supports inserting new users, checking if a username exists,
 * and retrieving user details by username.
 *
 * <p>The schema is defined in {@link AuthDatabase#ensureSchema()}:
 * <pre>
 * CREATE TABLE IF NOT EXISTS LoginRegisterUI (
 *   Username      TEXT PRIMARY KEY,
 *   PasswordHash  TEXT NOT NULL,
 *   StudentEmail  TEXT NOT NULL,
 *   GradeYearLevel INTEGER NOT NULL
 * );
 * </pre>
 */
public class UserDao {

    public boolean insert(User u) throws SQLException {
        String sql = """
            INSERT INTO LoginRegisterUI
              (Username, PasswordHash, StudentEmail, GradeYearLevel)
            VALUES (?, ?, ?, ?);
        """;
        try (PreparedStatement ps = AuthDatabase.get().prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getStudentEmail());
            ps.setInt(4, u.getGradeYearLevel());
            return ps.executeUpdate() == 1;
        }
    }

    public Optional<User> findByUsername(String username) throws SQLException {
        String sql = """
            SELECT Username, PasswordHash, StudentEmail, "GradeYearLevel"
            FROM LoginRegisterUI
            WHERE Username = ?;
        """;
        try (PreparedStatement ps = AuthDatabase.get().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(new User(
                        rs.getString("Username"),
                        rs.getString("PasswordHash"),
                        rs.getString("StudentEmail"),
                        rs.getInt("GradeYearLevel")
                ));
            }
        }
    }

    public boolean exists(String username) throws SQLException {
        String sql = "SELECT 1 FROM LoginRegisterUI WHERE Username = ? LIMIT 1;";
        try (PreparedStatement ps = AuthDatabase.get().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) throws SQLException {
        String sql = "SELECT * FROM LoginRegisterUI WHERE username = ? OR StudentEmail = ?";
        try( Connection conn = AuthDatabase.get();
             PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, usernameOrEmail);
            stmt.setString(2, usernameOrEmail);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                return Optional.of(mapRowToUser(rs));
            }
            return Optional.empty();
        }
    }
    public void saveResetToken(String username, String token, long expiry) throws SQLException{
        String sql = "UPDATE LoginRegisterUI SET reset_token = ?, reset_expiry = ? Where username = ?";
        try (Connection conn = AuthDatabase.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            stmt.setLong(2, expiry);
            stmt.setString(3, username);
            stmt.executeUpdate();
        }

    }
    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User(
                rs.getString("Username"),
                rs.getString("PasswordHash"),
                rs.getString("StudentEmail"),
                rs.getInt("GradeYearLevel")
        );

        // Safely read reset columns if they exist
        try {
            user.setResetToken(rs.getString("reset_token"));
        } catch (SQLException ignore) {
            user.setResetToken(null);
        }

        try {
            user.setResetExpiry(rs.getLong("reset_expiry"));
        } catch (SQLException ignore) {
            user.setResetExpiry(0L);
        }

        return user;
    }
    public Optional<User> findByResetToken(String token) throws SQLException{
        String sql = "SELECT * FROM LoginRegisterUI WHERE reset_token = ?";
        try(Connection conn = AuthDatabase.get();
        PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return Optional.of(mapRowToUser(rs));
            }
            return Optional.empty();
        }
    }
    public void updatePassword(String username, String newPassword) throws SQLException{
        String sql = "UPDATE LoginRegisterUI SET PasswordHash = ? Where Username = ?";
        try (Connection conn = AuthDatabase.get();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, newPassword);
            stmt.setString(2, username);
            stmt.executeUpdate();
        }

    }
    public void clearResetToken(String username) throws SQLException{
        String sql = "UPDATE LoginRegisterUI SET reset_token = NULL, reset_expiry = NULL WHERE Username = ?";
        try (Connection conn = AuthDatabase.get();
             PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }

}
