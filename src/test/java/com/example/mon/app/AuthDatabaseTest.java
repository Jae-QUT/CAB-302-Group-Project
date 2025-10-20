package com.example.mon.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the AuthDatabase class.
 * Ensures database connection and schema setup work correctly.
 *
 * @author Testing Team
 */
class AuthDatabaseTest {

    @BeforeEach
    void setUp() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        AuthDatabase.override(conn);
    }

    /**
     * Tests that database connection is established.
     */
    @Test
    void testGetConnection() throws SQLException {
        Connection conn = AuthDatabase.get();
        assertNotNull(conn);
        assertFalse(conn.isClosed());
    }

    /**
     * Tests that schema is created without errors.
     */
    @Test
    void testEnsureSchema() {
        assertDoesNotThrow(AuthDatabase::ensureSchema);
    }

    /**
     * Tests that table exists after schema creation.
     */
    @Test
    void testTableExists() throws SQLException {
        AuthDatabase.ensureSchema();

        var meta = AuthDatabase.get().getMetaData();
        var rs = meta.getTables(null, null, "LoginRegisterUI", null);

        assertTrue(rs.next());
    }
}