package com.example.mon.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:/Users/uni/Documents/auth/MonDatabase.db";  // Path to your DB file

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                System.out.println("Connected to SQLite!");

                // Quick test: list all monsters
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT monster_id, name, math_type, base_hp FROM MathMonsterMons");

                while (rs.next()) {
                    System.out.println(
                            rs.getInt("monster_id") + " | " +
                                    rs.getString("name") + " | " +
                                    rs.getString("math_type") + " | HP: " +
                                    rs.getInt("base_hp")
                    );
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
