package com.therejects.cab302groupproject.controller;

import com.therejects.cab302groupproject.model.User;
import com.therejects.cab302groupproject.model.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.Optional;

public class ProfileController {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private TextField yearLevelField;

    private final UserDAO userDAO = new UserDAO();
    private User currentUser;

    public void loadProfile(String username) {
        try {
            Optional<User> result = userDAO.findByUsername(username);
            if (result.isPresent()) {
                currentUser = result.get();
                usernameField.setText(currentUser.getUsername());
                emailField.setText(currentUser.getStudentEmail());
                yearLevelField.setText(String.valueOf(currentUser.getGradeYearLevel()));
            } else {
                showAlert(Alert.AlertType.ERROR, "User not found");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database error loading profile");
        }
    }

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

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}