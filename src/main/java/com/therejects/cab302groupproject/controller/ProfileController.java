package com.therejects.cab302groupproject.controller;

import com.example.mon.app.*;
import com.therejects.cab302groupproject.Navigation.ScreenManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.function.Consumer;

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
    private Consumer<String> navigator;
    @FXML private StackPane root;
    @FXML private Button backButton;

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

        currentUser.setGamesPlayed(10);
        currentUser.setGamesWon(7);
        currentUser.setGamesLost(3);
        currentUser.getBadges().addAll(java.util.List.of("Math Novice", "Quick Thinker"));
        currentUser.getFriends().addAll(java.util.List.of("Alice", "Bob"));

        refreshExtras();
    }

    /**
     * Refresh stats, badges and friend sections with data from current user
     */
    private void refreshExtras() {
        User currentUser = User.getCurrentUser();
        statsLabel.setText("Games: " + currentUser.getGamesPlayed()
                + " | Won: " + currentUser.getGamesWon()
                + " | Lost: " + currentUser.getGamesLost());

        badgesList.getItems().setAll(currentUser.getBadges());
        friendsList.getItems().setAll(currentUser.getFriends());
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

//    }
////        stepBack.setPrefWidth(150);
//        stepBack.setPrefHeight(44);
//        backButton.setOnAction(e -> {12345678
//        );

    }