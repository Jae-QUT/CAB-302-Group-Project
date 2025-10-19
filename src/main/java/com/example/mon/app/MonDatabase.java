package com.example.mon.app;

import java.sql.*;

public final class MonDatabase {
    private static final String URL = "mathmonsters.db"; // path to your .db
    private static Connection conn;

    private MonDatabase() {}

    public static synchronized Connection get() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(URL);
        }
        return conn;
    }


    public static void ensureSchema() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS LoginRegisterUI(
              Username TEXT NOT NULL PRIMARY KEY,
              Password TEXT NOT NULL,
              StudentEmail TEXT NOT NULL,
              "Grade/Year Level" INTEGER NOT NULL
            );
        """;
        try (Statement st = get().createStatement()) { st.execute(sql); }
    }
}
