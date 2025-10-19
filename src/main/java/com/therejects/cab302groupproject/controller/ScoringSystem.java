package com.therejects.cab302groupproject.controller;

import com.therejects.cab302groupproject.model.*;
import java.sql.SQLException;

public class ScoringSystem {
    public int delta = 0;
    private boolean isWinner;

    public int calculateDelta(boolean correct){
        if(correct){
            delta = delta + 1;
        }else {
            delta = delta - 1;
        }
        System.out.println(delta);
        return delta;
    }

    /**
     *
     * @param username This is the current user's identifier. We
     * @param delta This is the change in score. The increase or decrease in the stored value
     * @return The updated score into the user database
     * @throws SQLException
     */
    public boolean addToScore(String username, int delta) throws SQLException {
        final String sql =
                "UPDATE LoginRegisterUI SET Score = COALESCE(Score, 0) + ? WHERE Username = ?";

        try (var ps = AuthDatabase.get().prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setString(2, username);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean subtractFromScore(String username, int delta) throws SQLException {
        final String sql =
                "UPDATE LoginRegisterUI SET Score = COALESCE(Score, 0) - ? WHERE Username = ?";
        try (var ps = AuthDatabase.get().prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setString(2, username);
            return ps.executeUpdate() == 1;
        }
    }
}
