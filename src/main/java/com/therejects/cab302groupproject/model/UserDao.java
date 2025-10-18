package com.therejects.cab302groupproject.model;

import com.example.mon.app.AuthDatabase;
import com.example.mon.app.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                      (Username, PasswordHash, StudentEmail, "GradeYearLevel", "Score")
                    VALUES (?, ?, ?, ?, ?);
                """;
        try (PreparedStatement ps = AuthDatabase.get().prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getStudentEmail());
            ps.setInt(4, u.getGradeYearLevel());
            ps.setInt(5, u.getScore());
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
                    SELECT Username, PasswordHash, StudentEmail, "GradeYearLevel", "Score"
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
                        rs.getInt("GradeYearLevel"),
                        rs.getInt("Score")
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
        try (PreparedStatement ps = com.example.mon.app.Database.get().prepareStatement(sql)) {
            ps.setString(1, u.getStudentEmail());
            ps.setInt(2, u.getGradeYearLevel());
            ps.setString(3, u.getUsername());
            return ps.executeUpdate() == 1;
        }
    }
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) throws SQLException {
        String sql = "SELECT * FROM LoginRegisterUI Where Username = ? OR StudentEmail = ?";
        try (Connection conn = com.example.mon.app.Database.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usernameOrEmail);
            stmt.setString(2, usernameOrEmail);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
            return Optional.empty();
        }
    }
    public void saveResetToken(String username, String token, long expiry) throws SQLException {
        String sql = "UPDATE LoginRegisterUI SET reset_token = ?, reset_expiry = ? WHERE Username = ?";
        try (Connection conn = com.example.mon.app.Database.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            stmt.setLong(2, expiry);
            stmt.setString(3, username);
            stmt.executeUpdate();
        }
    }
    public Optional<User> findByResetToken(String token) throws SQLException {
        String sql = "SELECT * FROM LoginRegisterUI WHERE reset_token = ?";
        try (Connection conn = com.example.mon.app.Database.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
            return Optional.empty();
        }
    }
    public void updatePassword(String username, String password) throws SQLException {
        String sql = "UPDATE LoginRegisterUI SET Password = ? WHERE Username = ?";
        try (Connection conn = com.example.mon.app.Database.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, password);
            stmt.setString(2, username);
            stmt.executeUpdate();
        }
    }
    public void clearResetToken(String username) throws SQLException {
        String sql = "UPDATE LoginRegisterUI SET reset_token = NULL, reset_expiry = NULL WHERE Username = ?";
        try (Connection conn = Database.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }
    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User(
                rs.getString("Username"),
                rs.getString("Password"),
                rs.getString("StudentEmail"),
                rs.getInt("Grade/Year Level"),
                rs.getInt("Score")

        );
        u.setResetToken(rs.getString("reset_token"));
        u.setResetExpiry(rs.getLong("reset_expiry"));
        return u;
    }
}
