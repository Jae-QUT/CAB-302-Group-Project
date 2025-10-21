package com.therejects.cab302groupproject.model;

import java.sql.*;

public final class Database {
    private static final String URL = "jdbc:sqlite:mathmonsters.db"; // path to your .db
    private static Connection conn;

    private Database() {}

    public static synchronized Connection get() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(URL);
        }
        return conn;
    }

    // Call once on startup to ensure table exists (safe if it already exists)
    public static void ensureSchema() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS LoginRegisterUI(
              Username TEXT NOT NULL PRIMARY KEY,
              Password TEXT NOT NULL,
              StudentEmail TEXT NOT NULL,
              "Grade/Year Level" INTEGER NOT NULL,
              reset_token TEXT,
              reset_expiry INTEGER
            );
        """;
        try (Statement st = get().createStatement()) { st.execute(sql); }
    }
}
