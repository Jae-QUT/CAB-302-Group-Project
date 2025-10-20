package com.therejects.cab302groupproject.controller;

import com.therejects.cab302groupproject.MainMenuLauncher;
import com.therejects.cab302groupproject.Navigation.ScreenManager;
import com.therejects.cab302groupproject.model.AuthService;
import com.therejects.cab302groupproject.model.EmailService;
import com.therejects.cab302groupproject.model.User;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * A controller class responsible for managing the login screen of the application.
 * It handles the initialization of the hero image, validates user credentials, and
 * provides interactivity for the login and "View More" actions.
 */
 public class LoginController {
    @FXML private ImageView heroImage;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMe;

    /**
     * Initializes the login screen by loading and displaying the hero image.
     */
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

    /**
     * Handles the login action by validating credentials and showing alerts.
     */
    @FXML
    private void onLogin() {
        String u = usernameField.getText();
        String p = passwordField.getText();

        if (u == null || u.isBlank() || p == null || p.length() < 4) {
            new Alert(Alert.AlertType.ERROR, "Invalid credentials. Try again.").showAndWait();
            return;
        }else{
            new Alert(Alert.AlertType.INFORMATION, "Welcome, " + u + "!").showAndWait();

        }

        Stage stage = (Stage) usernameField.getScene().getWindow();
        ScreenManager sm = new ScreenManager(stage);
        sm.navigateTo("MAIN_MENU");
        stage.show();
    }

    /**
     * Displays additional information about upcoming features.
     */
    @FXML
    private void onViewMore() {
        new Alert(Alert.AlertType.INFORMATION, "Coming soon: trailer / feature rundown.").showAndWait();
    }

    @FXML
    private void onForgotPassword() {
        System.out.println("Forgot password clicked");
        TextInputDialog emailDialog = new TextInputDialog();
        emailDialog.setTitle("Forgot Password");
        emailDialog.setHeaderText("Reset your PokéMath password");
        emailDialog.setContentText("Enter your email or username:");

        emailDialog.showAndWait().ifPresent(input -> {
            try {
                String token = authService.generateResetToken(input);
                String email = authService.getEmailForUser(input);

                // Send token (or simulate)
                emailService.sendPasswordResetEmail(email, token);
                System.out.println("Reset token for " + email + ": " + token); // debug

                new Alert(Alert.AlertType.INFORMATION,
                        "A password reset email has been sent to " + email + ".\nCheck your inbox.")
                        .showAndWait();

                openResetDialog();

            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR,
                        "Error: " + e.getMessage()).showAndWait();
            }
        });
    }

    private void openResetDialog() {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Reset Password");
        dialog.setHeaderText("Enter your reset token and new password");

        Label tokenLabel = new Label("Reset Token:");
        TextField tokenField = new TextField();

        Label passLabel = new Label("New Password:");
        PasswordField passField = new PasswordField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(tokenLabel, 0, 0);
        grid.add(tokenField, 1, 0);
        grid.add(passLabel, 0, 1);
        grid.add(passField, 1,  1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if(button == ButtonType.OK){
                return new String[]{tokenField.getText(), passField.getText()};
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            String token = result[0];
            String newPassword = result[1];
            try {
                authService.resetPassword(token, newPassword);
                new Alert(Alert.AlertType.INFORMATION,
                        "Password reset successful! You can now log in with your new password.")
                        .showAndWait();
            } catch (Exception e) {
                new Alert(Alert.AlertType.WARNING, e.getMessage()).showAndWait();
            }
        });

    }
    @FXML
    private void onRegister() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Create Account");
        dialog.setHeaderText("Register your Math Monsters account!");

      /// CSS styling///
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/ui/register.css").toExternalForm()
        );

        // --- Labels and Inputs ---
        Label userLabel = new Label("Username:");
        TextField userField = new TextField();

        Label passLabel = new Label("Password:");
        PasswordField passField = new PasswordField();

        Label confirmLabel = new Label("Confirm Password:");
        PasswordField confirmField = new PasswordField();

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();

        Label gradeLabel = new Label("Year Level:");
        Spinner<Integer> gradeSpinner = new Spinner<>(1, 12, 1);

        /// Prompt Text///
        userField.setPromptText("choose a username");
        emailField.setPromptText("Your student email address");
        passField.setPromptText("Enter a strong Password");
        confirmField.setPromptText("Confirm your password");

        // --- Layout ---
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        grid.add(userLabel, 0, 0);
        grid.add(userField, 1, 0);
        grid.add(passLabel, 0, 1);
        grid.add(passField, 1, 1);
        grid.add(confirmLabel, 0, 2);
        grid.add(confirmField, 1, 2);
        grid.add(emailLabel, 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(gradeLabel, 0, 4);
        grid.add(gradeSpinner, 1, 4);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // --- Handle dialog result ---
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                if (!passField.getText().equals(confirmField.getText())) {
                    new Alert(Alert.AlertType.ERROR, "Passwords do not match!").showAndWait();
                    return null;
                }
                return new User(
                        userField.getText(),
                        passField.getText(),
                        emailField.getText(),
                        gradeSpinner.getValue(),
                        0 // initial score
                );
            }
            return null;
        });

        // --- Process the result ---
        dialog.showAndWait().ifPresent(user -> {
            try {
                authService.register(
                        user.getUsername(),
                        user.getPassword(),
                        user.getStudentEmail(),
                        user.getGradeYearLevel(),
                        user.getScore()
                );
                new Alert(Alert.AlertType.INFORMATION,
                        "Account created successfully! You can now log in.").showAndWait();

                usernameField.setText(user.getUsername());
                passwordField.setText(user.getPassword());
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR,
                        "Registration Failed: " + e.getMessage()).showAndWait();
            }
        });
    }

    private final AuthService authService = new AuthService();
    private final EmailService emailService = new EmailService();
}
