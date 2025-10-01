package com.example.mon.app;

import java.sql.*;

/**
 * Links the database URL to so the connection knows what to link to and ensures that the data input matches the schema provided
 */
public final class Database {
    private static final String URL = "jdbc:sqlite:mathmonsters.db"; // path to your .db
    private static Connection conn;

    private Database() {}

    /**
     * Connection get() is the link to the database and allows us to see if the connection is available or if it is closed
     * for any reason.
     * @return Returns the connection to the Database
     * @throws SQLException Throws a specific exception in the case of SQL errors for better management purposes
     */
    public static synchronized Connection get() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(URL);
        }
        return conn;
    }


    /**
     * Ensures that mon creation happens in the exact way we want it to
     * @throws SQLException Throws a specific exception in the case of SQL errors for better management purposes
     */
    // Call once on startup to ensure table exists (safe if it already exists)
    // -- THIS IS A COPY OF AUTHDATABASE REBUILD THIS WHEN USED --
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
