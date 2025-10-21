package com.therejects.cab302groupproject.controller;

import com.therejects.cab302groupproject.Navigation.ScreenManager;
// IMPORTANT: use your actual AuthDatabase package:
import com.example.mon.app.AuthDatabase;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardController {
    private static final int PAGE_SIZE = 10;

    @FXML private Pagination leaderboardPage;
    @FXML private Button backToMenu;
    @FXML private TextField usernameSearch;
    @FXML private GridPane leaderboardGrid;

    private ScreenManager screenManager;

    public void setScreenManager(ScreenManager sm) { this.screenManager = sm; }

    private ScreenManager sm() {
        if (screenManager == null) {
            Stage stage = (Stage) backToMenu.getScene().getWindow();
            screenManager = new ScreenManager(stage);
        }
        return screenManager;
    }

    @FXML
    private void initialize() {
        // If you already call ensureSchema() at startup, you can remove this.
        try { com.example.mon.app.AuthDatabase.ensureSchema(); } catch (SQLException e) { e.printStackTrace(); }

        refreshPagination();
        usernameSearch.setOnAction(e -> onUsernameSearch());
    }

    @FXML
    protected void onBackToMainMenu() throws IOException {
        sm().navigateTo("MAIN_MENU");
    }

    @FXML
    protected void onUsernameSearch() {
        refreshPagination();
        leaderboardPage.setCurrentPageIndex(0);
    }

    // ---------------- Pagination ----------------

    private void refreshPagination() {
        String filter = safeLike(usernameSearch.getText());
        int total = getTotalCount(filter);

        int pageCount = (int) Math.ceil(Math.max(1, total) / (double) PAGE_SIZE);
        leaderboardPage.setPageCount(pageCount);
        leaderboardPage.setMaxPageIndicatorCount(10);

        // We render into a separate GridPane; return a dummy node to satisfy API.
        leaderboardPage.setPageFactory(pageIndex -> {
            populateGrid(pageIndex, filter);
            return new StackPane();
        });

        populateGrid(0, filter);
    }

    private void populateGrid(int pageIndex, String filter) {
        // Remove data rows (keep header/separator at row 0)
        leaderboardGrid.getChildren().removeIf(node -> {
            Integer ri = GridPane.getRowIndex(node);
            return ri != null && ri >= 1;
        });

        List<UserScore> page = getPage(filter, PAGE_SIZE, pageIndex * PAGE_SIZE);

        for (int i = 0; i < page.size(); i++) {
            int globalRank = pageIndex * PAGE_SIZE + i + 1;
            UserScore us = page.get(i);

            Label rank  = new Label(ordinal(globalRank));
            Label user  = new Label(us.username());
            Label score = new Label(String.valueOf(us.score()));

            rank.setFont(Font.font(16));
            user.setFont(Font.font(16));
            score.setFont(Font.font(16));

            addCell(rank,  0, i + 1);
            addCell(user,  1, i + 1);
            addCell(score, 2, i + 1);
        }

        if (page.isEmpty()) {
            Label empty = new Label("No results");
            addCell(empty, 1, 1);
        }
    }

    private void addCell(Node n, int col, int row) {
        GridPane.setColumnIndex(n, col);
        GridPane.setRowIndex(n, row);
        leaderboardGrid.getChildren().add(n);
    }

    // ---------------- DB ----------------

    private record UserScore(String username, int score) {}

    private static String safeLike(String s) {
        return (s == null) ? "" : s.trim().toLowerCase();
    }

    private int getTotalCount(String filter) {
        String sql = """
            SELECT COUNT(*)
            FROM LoginRegisterUI
            WHERE LOWER(Username) LIKE ?
        """;
        try (Connection c = AuthDatabase.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + filter + "%");
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private List<UserScore> getPage(String filter, int limit, int offset) {
        String sql = """
            SELECT Username, Score
            FROM LoginRegisterUI
            WHERE LOWER(Username) LIKE ?
            ORDER BY Score DESC, Username ASC
            LIMIT ? OFFSET ?
        """;
        List<UserScore> out = new ArrayList<>();
        try (Connection c = AuthDatabase.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + filter + "%");
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new UserScore(
                            rs.getString("Username"),
                            rs.getInt("Score")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

    // ---------------- Helpers ----------------

    private static String ordinal(int n) {
        int mod100 = n % 100, mod10 = n % 10;
        String suf = (mod100 >= 11 && mod100 <= 13) ? "th"
                : (mod10 == 1) ? "st"
                : (mod10 == 2) ? "nd"
                : (mod10 == 3) ? "rd" : "th";
        return n + suf;
    }
}
