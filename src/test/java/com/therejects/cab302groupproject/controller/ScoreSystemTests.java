package com.therejects.cab302groupproject.controller;

import com.example.mon.app.AuthDatabase;
import com.example.mon.app.User;
import com.example.mon.app.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the ScoringSystem class.
 * Verifies score calculations and database updates.
 *
 * @author Testing Team
 */
class ScoringSystemTest {
    private ScoringSystem scoring;
    private UserDao dao;

    @BeforeEach
    void setUp() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        conn.createStatement().execute("""
            CREATE TABLE LoginRegisterUI(
              Username TEXT PRIMARY KEY,
              PasswordHash TEXT NOT NULL,
              StudentEmail TEXT NOT NULL,
              "GradeYearLevel" INTEGER NOT NULL,
              "Score" INTEGER NOT NULL DEFAULT 0
            );
        """);
        AuthDatabase.override(conn);
        scoring = new ScoringSystem();
        dao = new UserDao();
    }

    /**
     * Tests that delta increases for correct answers.
     */
    @Test
    void testCalculateDeltaCorrect() {
        int result = scoring.calculateDelta(true);
        assertEquals(1, result);
        assertEquals(1, scoring.delta);
    }

    /**
     * Tests that delta decreases for incorrect answers.
     */
    @Test
    void testCalculateDeltaIncorrect() {
        int result = scoring.calculateDelta(false);
        assertEquals(-1, result);
        assertEquals(-1, scoring.delta);
    }

    /**
     * Tests multiple calculations accumulate correctly.
     */
    @Test
    void testDeltaAccumulation() {
        scoring.calculateDelta(true);  // +1
        scoring.calculateDelta(true);  // +1
        scoring.calculateDelta(false); // -1
        assertEquals(1, scoring.delta);
    }

    /**
     * Tests adding score to database.
     */
    @Test
    void testAddToScore() throws Exception {
        User user = new User("player1", "hash", "p@qut.edu.au", 5, 100);
        dao.insert(user);

        assertTrue(scoring.addToScore("player1", 10));

        User updated = dao.findByUsername("player1")
                .orElseThrow(() -> new AssertionError("User not found after insert"));
        assertEquals(110, updated.getScore());
    }

    /**
     * Tests subtracting score from database.
     */
    @Test
    void testSubtractFromScore() throws Exception {
        User user = new User("player2", "hash", "p2@qut.edu.au", 6, 100);
        dao.insert(user);

        assertTrue(scoring.subtractFromScore("player2", 15));

        User updated = dao.findByUsername("player2")
                .orElseThrow(() -> new AssertionError("User not found after insert"));
        assertEquals(85, updated.getScore());
    }

    /**
     * Tests that score updates fail for non-existent users.
     */
    @Test
    void testScoreUpdateNonExistentUser() throws Exception {
        assertFalse(scoring.addToScore("ghost", 10));
    }
}