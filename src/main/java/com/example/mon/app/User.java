package com.example.mon.app;

public class User {
    private String username;
    private String passwordHash;
    private String studentEmail;
    private int gradeYearLevel;

    public User() { }

    public User(String username, String password, String studentEmail, int gradeYearLevel) {
        this.username = username;
        this.passwordHash = password;
        this.studentEmail = studentEmail;
        this.gradeYearLevel = gradeYearLevel;
    }

    // Getters
    public String getUsername()      { return username; }
    public String getPassword()      { return passwordHash; }
    public String getStudentEmail()  { return studentEmail; }
    public int    getGradeYearLevel(){ return gradeYearLevel; }

    // Setters
    public void setUsername(String username)            { this.username = username; }
    public void setPassword(String password)            { this.passwordHash = password; }
    public void setStudentEmail(String studentEmail)    { this.studentEmail = studentEmail; }
    public void setGradeYearLevel(int gradeYearLevel)   { this.gradeYearLevel = gradeYearLevel; }


    @Override
    public String toString(){
        return "User{" +
                "username='" + username + '\'' +
                ", studentEmail='" + studentEmail + '\'' +
                ", gradeYearLevel=" + gradeYearLevel +
                '}';
    }
    private String resetToken;
    private long resetExpiry;

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public long getResetExpiry() {
        return resetExpiry;
    }

    public void setResetExpiry(long resetExpiry) {
        this.resetExpiry = resetExpiry;
    }

}
