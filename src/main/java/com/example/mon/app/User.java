package com.example.mon.app;

public class User {
    private String username;
    private String password;
    private String studentEmail;
    private int gradeYearLevel;

    public User() { }

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

    // Setters
    public void setUsername(String username)            { this.username = username; }
    public void setPassword(String password)            { this.password = password; }
    public void setStudentEmail(String studentEmail)    { this.studentEmail = studentEmail; }
    public void setGradeYearLevel(int gradeYearLevel)   { this.gradeYearLevel = gradeYearLevel; }
}
