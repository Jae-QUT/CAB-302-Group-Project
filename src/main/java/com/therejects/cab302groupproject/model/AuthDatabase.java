package com.therejects.cab302groupproject.model;
import java.sql.*;

/**
 * Links the database URL to so the connection knows what to link to and ensures that the data input matches the schema provided
 */
public final class AuthDatabase {
    // separate DB file just for users/auth:
    private static final String URL = "jdbc:sqlite:auth.db";
    private static Connection conn;

    private AuthDatabase() {
    }

    /**
     * Connection get() is the link to the database and allows us to see if the connection is avaliable or if it is closed
     * for any reason.
     *
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
     */
    public static void ensureSchema() throws SQLException {

    }

    /**
     * Adds a missing column to LoginRegisterUI if it doesn't already exist.
     */
    private static void addColumnIfMissing(String columnName, String columnType) {
        String checkSql = "PRAGMA table_info(LoginRegisterUI);";
        String alterSql = "ALTER TABLE LoginRegisterUI ADD COLUMN " + columnName + " " + columnType + ";";

        try (Connection conn = get();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(checkSql)) {

            boolean exists = false;
            while (rs.next()) {
                if (rs.getString("name").equalsIgnoreCase(columnName)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                st.execute(alterSql);
                System.out.println("[AuthDatabase] Added missing column: " + columnName);
            }

        } catch (SQLException e) {
            // Ignore harmless duplicate column errors
            if (!e.getMessage().toLowerCase().contains("duplicate column")) {
                e.printStackTrace();
            }
        }
    }

    public static void override(Connection conn) {
    }
}

