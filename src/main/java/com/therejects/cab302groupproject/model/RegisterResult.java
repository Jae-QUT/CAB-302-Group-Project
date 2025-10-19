package com.therejects.cab302groupproject.model;

public class RegisterResult {
    public final String username;
    public final String email;
    public final int gradeYearLevel;
    public final String password;
    public int score;

    public RegisterResult(String username, String email, int gradeYearLevel, String password){
        this.username = username;
        this.email = email;
        this.gradeYearLevel = gradeYearLevel;
        this.password = password;
        this.score = 0;
    }

}

