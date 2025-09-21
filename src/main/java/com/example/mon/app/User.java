package com.example.mon.app;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that attaches the Users information as well as stats for their battles for the sake of their profile pages
 * and the leaderboard.
 */
public class User {
    private String username;
    private String password;
    private String studentEmail;
    private int gradeYearLevel;

    // Profile extras
    private int gamesPlayed;
    private int gamesWon;
    private int gamesLost;
    private List<String> badges = new ArrayList<>();
    private List<String> friends = new ArrayList<>();

    /**
     * The constructor for the {@link User} to assign their details for the current instance of the app.
     * @param username Is the primary key and the identifier for the student
     * @param password Hashed password for privacy of the user.
     * @param studentEmail
     * @param gradeYearLevel
     */
    public User(String username, String password, String studentEmail, int gradeYearLevel) {
        this.username = username;
        this.password = password;
        this.studentEmail = studentEmail;
        this.gradeYearLevel = gradeYearLevel;
    }

    // Getters
    public String getUsername()      { return username; }
    public String getPassword()      { return password; }
    public String getStudentEmail()  { return studentEmail; }
    public int    getGradeYearLevel(){ return gradeYearLevel; }
    public int getGamesPlayed() { return gamesPlayed; }
    public int getGamesWon() { return gamesWon; }
    public int getGamesLost() { return gamesLost; }
    public List<String> getBadges() { return badges; }
    public List<String> getFriends() { return friends; }


    // Setters
    public void setUsername(String username)            { this.username = username; }
    public void setPassword(String password)            { this.password = password; }
    public void setStudentEmail(String studentEmail)    { this.studentEmail = studentEmail; }
    public void setGradeYearLevel(int gradeYearLevel)   { this.gradeYearLevel = gradeYearLevel; }
    public void setGamesPlayed(int gamesPlayed) { this.gamesPlayed = gamesPlayed; }
    public void setGamesWon(int gamesWon) { this.gamesWon = gamesWon; }
    public void setGamesLost(int gamesLost) { this.gamesLost = gamesLost; }
    public void setBadges(List<String> badges) { this.badges = badges; }
    public void setFriends(List<String> friends) { this.friends = friends; }
}
