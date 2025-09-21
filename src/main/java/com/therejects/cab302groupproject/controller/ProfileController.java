package com.therejects.cab302groupproject.controller;

import com.therejects.cab302groupproject.Navigation.ScreenManager;
import com.therejects.cab302groupproject.model.*;           // * means "ALL". Takes ALL things from that menu
//import com.therejects.cab302groupproject.model.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Optional;
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

    private final UserDAO userDAO = new UserDAO();
    private User currentUser;

    /**
     * Loads the profile data for the given username from SQlite DB, and populates fields.
     *
     * @param username the username of the profile to load
     */
    public void loadProfile(String username) {
        try {
            Optional<User> result = userDAO.findByUsername(username);
            if (result.isPresent()) {
                currentUser = result.get();
                usernameField.setText(currentUser.getUsername());
                emailField.setText(currentUser.getStudentEmail());
                yearLevelField.setText(String.valueOf(currentUser.getGradeYearLevel()));

                currentUser.setGamesPlayed(10);
                currentUser.setGamesWon(7);
                currentUser.setGamesLost(3);
                currentUser.getBadges().addAll(java.util.List.of("Math Novice", "Quick Thinker"));
                currentUser.getFriends().addAll(java.util.List.of("Alice", "Bob"));

                refreshExtras();
            } else {
                showAlert(Alert.AlertType.ERROR, "User not found");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database error loading profile");
        }
    }

    /**
     * Refresh stats, badges and friend sections with data from current user
     */
    private void refreshExtras() {
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
        if (currentUser == null) return;

        try {
            currentUser.setStudentEmail(emailField.getText());
            currentUser.setGradeYearLevel(Integer.parseInt(yearLevelField.getText()));

            boolean success = userDAO.updateProfile(currentUser);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Profile updated successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Update failed.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Year Level must be a number.");
        } catch (SQLException e) {
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