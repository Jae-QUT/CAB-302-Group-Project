package com.therejects.cab302groupproject.controller;

import com.therejects.cab302groupproject.MainMenuLauncher;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

public class LoginController {
    @FXML private ImageView heroImage;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMe;

    @FXML
    public void initialize() {
        var url = MainMenuLauncher.class.getResource("/images/MMLogin.png");
        if (url != null) {
            heroImage.setImage(new javafx.scene.image.Image(url.toExternalForm()));
        } else {
            System.err.println("Missing /images/MMLogin.png on classpath. " +
                    "Put it under src/main/resources/images/ and rebuild.");
        }
    }


    @FXML
    private void onLogin() {
        String u = usernameField.getText();
        String p = passwordField.getText();

        if (u == null || u.isBlank() || p == null || p.length() < 4) {
            new Alert(Alert.AlertType.ERROR, "Invalid credentials. Try again.").showAndWait();
            return;
        }
        new Alert(Alert.AlertType.INFORMATION, "Welcome, " + u + "!").showAndWait();
        
    }

    @FXML
    private void onViewMore() {
        new Alert(Alert.AlertType.INFORMATION, "Coming soon: trailer / feature rundown.").showAndWait();
    }
}
