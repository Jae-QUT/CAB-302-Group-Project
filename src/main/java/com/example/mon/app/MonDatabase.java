package com.example.mon.app;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Links the database URL to so the connection knows what to link to and ensures that the data input matches the schema provided
 */
public final class MonDatabase {
    private static final String URL = "jdbc:sqlite:mathmonsters.db"; // path to your .db
    private static Connection conn;

    private MonDatabase() {}

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
    public static void ensureMonsterSchema() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS monsters (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                spritePath TEXT NOT NULL,
                maxHp INTEGER NOT NULL
            );
        """;
        try (Statement st = get().createStatement()) { st.execute(sql); }
    }

    /**
     * Retrieves monsters from the database. This method executes an SQL query to fetch the name,
     * sprite path, and maximum HP (50) of monsters from the "Monsters" table
     *
     * @return a list of {@link Monster} objects each representing a monster retrieved from the database.
     * @throws SQLException Throws a specific exception in the case of SQL errors for better management purposes
     */
    public static List<Monster> getAllMonsters() throws SQLException {
        List<Monster> monsters = new ArrayList<>();
        String sql = "SELECT name, spritePath, maxHp FROM Monsters";

        try (Connection conn = get();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                monsters.add(new Monster(
                        rs.getString("name"),
                        rs.getString("spritePath"),
                        rs.getInt("maxHp")
                ));
            }
        }
        return monsters;
    }

    public static void override(Connection c) {
        conn = c;
    }
}
