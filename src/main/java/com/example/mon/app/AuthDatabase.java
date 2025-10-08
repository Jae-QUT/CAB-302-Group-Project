package com.example.mon.app;
import java.sql.*;

public final class AuthDatabase {
    // separate DB file just for users/auth:
    private static final String URL = "jdbc:sqlite:auth.db";
    private static Connection conn;

    private AuthDatabase() {
    }

    public static synchronized Connection get() throws SQLException {
        if (conn == null || conn.isClosed()) conn = DriverManager.getConnection(URL);
        return conn;
    }

    /**
     * Only the auth schema lives here; no monster tables
     */
    public static void ensureSchema() throws SQLException {
        try (Statement st = get().createStatement()) {
            // --- Create main user table if it doesn't exist ---
            st.execute("""
                        CREATE TABLE IF NOT EXISTS LoginRegisterUI (
                            Username TEXT NOT NULL PRIMARY KEY,
                            PasswordHash TEXT NOT NULL,
                            StudentEmail TEXT NOT NULL,
                            GradeYearLevel INTEGER NOT NULL
                            
                        );
                    """);

            // --- Add columns for password reset (safe to run repeatedly) ---
            try {
                st.execute("ALTER TABLE LoginRegisterUI ADD COLUMN reset_token TEXT;");
            } catch (SQLException ignore) {
                // Column already exists — ignore
            }

            try {
                st.execute("ALTER TABLE LoginRegisterUI ADD COLUMN reset_expiry INTEGER;");
            } catch (SQLException ignore) {
                // Column already exists — ignore
            }
        }
    }
}

