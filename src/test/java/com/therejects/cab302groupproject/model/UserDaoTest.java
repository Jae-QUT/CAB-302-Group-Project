package com.therejects.cab302groupproject.model;

import com.example.mon.app.UserDao;
import org.junit.jupiter.api.*;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.mon.app.AuthDatabase;
import com.example.mon.app.User;


public class UserDaoTest {
    private UserDao dao;

    @BeforeEach
    void setUp() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        conn.createStatement().execute("""
            CREATE TABLE LoginRegisterUI(
              Username TEXT PRIMARY KEY,
              PasswordHash TEXT NOT NULL,
              StudentEmail TEXT NOT NULL,
              "GradeYearLevel" INTEGER NOT NULL,
              "Score" INTEGER NOT NULL
            );
        """);
        AuthDatabase.override(conn); // add static setter in AuthDatabase
        dao = new UserDao();
    }

    @Test
    void testInsertAndFind() throws Exception {
        User u = new User("bob", "pw123", "bob@qut.edu.au", 10, 150);
        assertTrue(dao.insert(u));

        var found = dao.findByUsername("bob");
        assertTrue(found.isPresent());
        assertEquals("bob@qut.edu.au", found.get().getStudentEmail());
    }

    @Test
    void testExists() throws Exception {
        User u = new User("alice", "pw321", "alice@qut.edu.au", 9, 35);
        dao.insert(u);
        assertTrue(dao.exists("alice"));
        assertFalse(dao.exists("ghost"));
    }

    @Test
    void testUpdateProfile() throws Exception {
        User u = new User("sam", "pw", "s@qut.edu.au", 7, 50);
        dao.insert(u);

        u.setStudentEmail("new@qut.edu.au");
        u.setGradeYearLevel(8);
        assertTrue(dao.updateProfile(u));

        var updated = dao.findByUsername("sam").get();
        assertEquals("new@qut.edu.au", updated.getStudentEmail());
        assertEquals(8, updated.getGradeYearLevel());
    }
}



