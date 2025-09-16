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
}
