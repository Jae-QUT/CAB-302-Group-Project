package com.therejects.cab302groupproject.model;

import java.sql.*;
import java.util.Optional;

public class UserDAO {

    public boolean insert(User u) throws SQLException {
        String sql = """
          INSERT INTO LoginRegisterUI
            (Username, Password, StudentEmail, "Grade/Year Level")
          VALUES (?, ?, ?, ?)
        """;
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getStudentEmail());
            ps.setInt(4, u.getGradeYearLevel());
            return ps.executeUpdate() == 1;
        }
    }

    public Optional<User> findByUsername(String username) throws SQLException {
        String sql = """
          SELECT Username, Password, StudentEmail, "Grade/Year Level"
          FROM LoginRegisterUI WHERE Username = ?
        """;
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                User u = new User(
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("StudentEmail"),
                        rs.getInt("Grade/Year Level")
                );
                return Optional.of(u);
            }
        }
    }

    public boolean exists(String username) throws SQLException {
        String sql = "SELECT 1 FROM LoginRegisterUI WHERE Username = ? LIMIT 1";
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

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
