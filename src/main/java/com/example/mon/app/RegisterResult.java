package com.example.mon.app;

public class RegisterResult {
    public final String username;
    public final String email;
    public final int gradeYearLevel;
    public final String password;

    public RegisterResult(String username, String email, int gradeYearLevel, String password){
        this.username = username;
        this.email = email;
        this.gradeYearLevel = gradeYearLevel;
        this.password = password;
    }

}

