package com.example.mon.app;

import javafx.scene.control.Dialog;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

public class ForgotPasswordDialog {

    private final AuthService auth = new AuthService();


    public void openForgotPasswordDialog(Stage owner) {
        Dialog<String> dialog = new TextInputDialog();
        dialog.setTitle("Forgot Password");
        dialog.setHeaderText("Enter your registered username or email:");
        dialog.initOwner(owner);

        dialog.showAndWait().ifPresent(input -> {
            try {
                String token = auth.generateResetToken(input);
                String email = auth.getEmailForUser(input);

                EmailService emailService = new EmailService();
                emailService.sendPasswordReset(email, token);

                new Alert(Alert.AlertType.INFORMATION,
                        "A reset token has been sent to your registered email.").showAndWait();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).showAndWait();
            }
        });
    }
}