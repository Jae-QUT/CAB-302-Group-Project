package com.therejects.cab302groupproject.model;

import com.example.mon.app.AuthDatabase;
import org.junit.jupiter.api.BeforeEach;
import java.sql.Connection;
import java.sql.DriverManager;

public abstract class BaseTest {
    @BeforeEach
    void resetDb() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        conn.createStatement().execute("""
                    CREATE TABLE LoginRegisterUI(
                      Username TEXT PRIMARY KEY,
                      PasswordHash TEXT NOT NULL,
                      StudentEmail TEXT NOT NULL,
                      GradeYearLevel INTEGER NOT NULL
                    );
                """);
        AuthDatabase.override(conn);
    }
}