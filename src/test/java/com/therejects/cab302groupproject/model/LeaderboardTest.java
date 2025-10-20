package com.therejects.cab302groupproject.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Leaderboard model class.
 * Validates leaderboard entry creation and data access.
 *
 * @author Testing Team
 */
class LeaderboardTest {

    /**
     * Tests creating a leaderboard entry with valid data.
     */
    @Test
    void testLeaderboardCreation() {
        Leaderboard entry = new Leaderboard("champion", 500);

        assertEquals("champion", entry.getUsername());
        assertEquals(500, entry.getScore());
    }

    /**
     * Tests leaderboard entry with zero score.
     */
    @Test
    void testZeroScore() {
        Leaderboard entry = new Leaderboard("newbie", 0);
        assertEquals(0, entry.getScore());
    }

    /**
     * Tests leaderboard entry with negative score.
     * (May happen if student gets many wrong answers)
     */
    @Test
    void testNegativeScore() {
        Leaderboard entry = new Leaderboard("struggling", -10);
        assertEquals(-10, entry.getScore());
    }
}