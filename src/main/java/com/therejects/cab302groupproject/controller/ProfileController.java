package com.therejects.cab302groupproject.controller;

import com.therejects.cab302groupproject.Navigation.ScreenManager;
import com.therejects.cab302groupproject.model.AuthDatabase;
import com.therejects.cab302groupproject.model.User;
import com.therejects.cab302groupproject.model.UserDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the profile page. Handles the display and update of
 * user details, stats, badges, friends
 */
public class ProfileController {
    private ScreenManager screenManager;

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private TextField yearLevelField;
    @FXML private Label statsLabel;
    @FXML private ListView<String> badgesList;
    @FXML private ListView<String> friendsList;


    private final UserDao userDao = new UserDao();


    @FXML
    public void initialize() {
        loadProfile();
    }

    /**
     * Loads the profile data for the given username from SQlite DB, and populates fields
     */
    public void loadProfile() {
        User currentUser = User.getCurrentUser();

        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "No user logged in");
            return;
        }
        usernameField.setText(currentUser.getUsername());
        emailField.setText(currentUser.getStudentEmail());
        yearLevelField.setText(String.valueOf(currentUser.getGradeYearLevel()));

        refreshStats();
        refreshBadges();
    }

    private void refreshStats() {
        User currentUser = User.getCurrentUser();

        int score = currentUser.getScore();
        int rank = getLeaderboardRank(currentUser.getUsername());

        String rankStr = (rank > 0) ? ordinal(rank) : "Unranked";

        statsLabel.setText(String.format(
                "Score: %d | Leaderboard Rank: %s | Year Level: %d",
                score, rankStr, currentUser.getGradeYearLevel()
        ));

        refreshFriends(currentUser.getUsername());
    }

    private void refreshFriends(String currentUsername) {
        List<String> friends = getAllUsersExceptCurrent(currentUsername);
        friendsList.getItems().setAll(friends);
    }

    private List<String> getAllUsersExceptCurrent(String currentUsername) {
        List<String> users = new ArrayList<>();
        String sql = "SELECT Username FROM LoginRegisterUI WHERE Username != ? ORDER BY Username ASC";

        try (Connection conn = AuthDatabase.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, currentUsername);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(rs.getString("Username"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    private void refreshBadges() {
        User currentUser = User.getCurrentUser();
        List<String> badges = calculateBadges(currentUser.getUsername(), currentUser.getScore());
        badgesList.getItems().setAll(badges);
    }

    private int getLeaderboardRank(String username) {
        String sql = """
            SELECT COUNT(*) + 1 as rank
            FROM LoginRegisterUI
            WHERE Score > (SELECT Score FROM LoginRegisterUI WHERE Username = ?)
        """;
        try (Connection conn = AuthDatabase.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("rank");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private List<String> calculateBadges(String username, int score) {
        List<String> badges = new ArrayList<>();

        badges.add("🎓 Welcome - Created an account");

        if (score >= 15) badges.add("Novice - Score 15+");
        if (score >= 50) badges.add("Rising Star - Score 50+");
        if (score >= 100) badges.add("Champion - Score 100+");
        if (score >= 500) badges.add("Master - Score 500+");

        int rank = getLeaderboardRank(username);
        if (rank == 1) badges.add("#1 - Top of the Leaderboard!");
        else if (rank <= 3 && rank > 0) badges.add("Top 3 - Podium Finish");
        else if (rank <= 10 && rank > 0) badges.add("Top 10 - Overachiever");

        return badges;
    }

    /**
     * Saves changes to the users profile, updates email and yr level in DB.
     */
    @FXML
    private void handleSave() {
        User currentUser = User.getCurrentUser();
        if (currentUser == null) return;

        try {
            currentUser.setStudentEmail(emailField.getText());
            currentUser.setGradeYearLevel(Integer.parseInt(yearLevelField.getText()));

            boolean success = userDao.updateProfile(currentUser);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Profile updated successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Update failed.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Year Level must be a number.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database error saving profile.");
        }
    }

    /**
     * Displays alert + message to user
     *
     * @param type error type
     * @param message error message
     */
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void stepBack(ActionEvent e) {
        Stage stage = (Stage) ((Button) e.getSource()).getScene().getWindow();
        if (screenManager == null) screenManager = new ScreenManager(stage);
        screenManager.navigateTo("MAIN_MENU");
    }

    private static String ordinal(int n) {
        int mod100 = n % 100, mod10 = n % 10;
        String suf = (mod100 >= 11 && mod100 <= 13) ? "th"
                : (mod10 == 1) ? "st"
                : (mod10 == 2) ? "nd"
                : (mod10 == 3) ? "rd" : "th";
        return n + suf;
    }

//    }
////        stepBack.setPrefWidth(150);
//        stepBack.setPrefHeight(44);
//        backButton.setOnAction(e -> {12345678
//        );

    }