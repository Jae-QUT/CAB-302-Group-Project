package com.example.mon.app;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

public class ResetPasswordDialog {

    private final AuthService auth = new AuthService();

    public void show(Stage owner) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Reset Password");
        dialog.setHeaderText("Enter your reset token and new password:");
        dialog.initOwner(owner);


        Label tokenLabel = new Label("Token:");
        TextField tokenField = new TextField();

        Label newPassLabel = new Label("New Password:");
        PasswordField newPassField = new PasswordField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(tokenLabel, 0, 0);
        grid.add(tokenField, 1, 0);
        grid.add(newPassLabel, 0, 1);
        grid.add(newPassField, 1, 1);

        dialog.getDialogPane().setContent(grid);


        ButtonType resetButtonType = new ButtonType("Reset", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(resetButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == resetButtonType) {
                return new Pair<>(tokenField.getText(), newPassField.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            String token = result.getKey();
            String newPassword = result.getValue();

            try {
                auth.resetPassword(token, newPassword);
                new Alert(Alert.AlertType.INFORMATION,
                        "Password successfully reset! You can now log in with your new password.")
                        .showAndWait();

            } catch (IllegalArgumentException e) {
                new Alert(Alert.AlertType.WARNING, e.getMessage()).showAndWait();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR,
                        "Error resetting password: " + e.getMessage()).showAndWait();
            }
        });
    }
}
