package com.example.mon.app;
import java.sql.*;

/**
 * Links the database URL to so the connection knows what to link to and ensures that the data input matches the schema provided
 */
public final class AuthDatabase {
    // separate DB file just for users/auth:
    private static final String URL = "jdbc:sqlite:/Users/uni/IdeaProjects/CAB-302-Group-Project/auth.db";
//    private static final String URL = "jdbc:sqlite:auth.db";
    private static Connection conn;

    private AuthDatabase() {}

    /**
     * Connection get() is the link to the database and allows us to see if the connection is avaliable or if it is closed
     * for any reason.
     * @return Returns the connection to the Database
     * @throws SQLException Throws a specific exception in the case of SQL errors for better management purposes
     */
    public static synchronized Connection get() throws SQLException {
        if (conn == null || conn.isClosed()) conn = DriverManager.getConnection(URL);
        return conn;
    }

    /**
     * Only the auth schema lives here; no monster tables
     * This Schema ensures that the user input when making an account matches what is expected from the User Storage.
     * Data can be pulled as needed based on the Username with the intention of using it for the profile and leaderboard
     * */
    public static void ensureSchema() throws SQLException {
        String sql = """
    CREATE TABLE IF NOT EXISTS LoginRegisterUI(
      Username TEXT NOT NULL PRIMARY KEY,
      PasswordHash TEXT NOT NULL,
      StudentEmail TEXT NOT NULL,
      GradeYearLevel INTEGER NOT NULL,
      Score          INTEGER NOT NULL DEFAULT 0
                
    );
""";
        try (Statement st = get().createStatement()) { st.execute(sql); }
    }
    public static void override(Connection c) {
        conn = c;
    }
}
