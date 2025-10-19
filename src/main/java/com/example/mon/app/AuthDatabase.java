package com.example.mon.app;
import java.sql.*;

public final class AuthDatabase {
    // separate DB file just for users/auth:
    private static final String URL = "jdbc:sqlite:C:/Users/xavec/IdeaProjects/CAB-302-Group-Project/auth.db";
    private static Connection conn;

    private AuthDatabase() {}

    public static synchronized Connection get() throws SQLException {
        if (conn == null || conn.isClosed()) conn = DriverManager.getConnection(URL);
        return conn;
    }

    /** Only the auth schema lives here; no monster tables */
    public static void ensureSchema() throws SQLException {
        String sql = """
                    CREATE TABLE IF NOT EXISTS LoginRegisterUI(
                      Username TEXT NOT NULL PRIMARY KEY,
                      PasswordHash TEXT NOT NULL,
                      StudentEmail TEXT NOT NULL,
                      GradeYearLevel INTEGER NOT NULL
                    );
                """;
        try (Statement st = get().createStatement()) {
            st.execute(sql);
        }
    }

    public static void override(Connection c) {
        conn = c;
    }
}
