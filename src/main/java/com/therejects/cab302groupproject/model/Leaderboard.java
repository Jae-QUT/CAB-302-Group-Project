package com.therejects.cab302groupproject.model;

import javafx.scene.control.*;


/**
 * The Leaderboard is a visual way of tracking the progress and skills of the students through their wins and losses
 * against other students in their class.
 */
public class Leaderboard {
    private String username;
    private int score;

    //Constructor
    public Leaderboard(String username, int score) {
        this.username = username;
        this.score = score;
    }
    // Getters and setters
    public String getUsername() {
        return username;
    }
    public int getScore() {
        return score;
    }

}
