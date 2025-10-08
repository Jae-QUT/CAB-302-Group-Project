package com.example.mon.app;

import java.sql.*;
import java.util.Optional;

/**
 *
 */
public class UserDao {
    /**
     *
     * @param u
     * @return
     * @throws SQLException
     */
    public boolean insert(User u) throws SQLException {
        String sql = """
                    INSERT INTO LoginRegisterUI
                      (Username, PasswordHash, StudentEmail, "GradeYearLevel")
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

    /**
     *
     * @param username
     * @return
     * @throws SQLException
     */
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

    /**
     *
     * @param username
     * @return
     * @throws SQLException
     */
    public boolean exists(String username) throws SQLException {
        String sql = "SELECT 1 FROM LoginRegisterUI WHERE Username = ? LIMIT 1;";
        try (PreparedStatement ps = AuthDatabase.get().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     *
     * @param u
     * @return
     * @throws SQLException
     */
    public boolean updateProfile(User u) throws SQLException {
        String sql = """
                  UPDATE LoginRegisterUI
                  SET StudentEmail = ?, "Grade/Year Level" = ?
                  WHERE Username = ?
                """;
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            ps.setString(1, u.getStudentEmail());
            ps.setInt(2, u.getGradeYearLevel());
            ps.setString(3, u.getUsername());
            return ps.executeUpdate() == 1;
        }
    }
}
