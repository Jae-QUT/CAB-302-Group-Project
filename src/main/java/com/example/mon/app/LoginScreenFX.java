package com.example.mon.app;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * JavaFX implementation of the login screen for the PokéMath application.
 * <p>
 * This class replaces the original Swing-based LoginScreen and provides:
 * <ul>
 *     <li>Username/email field</li>
 *     <li>Password field with show/hide option</li>
 *     <li>Remember me checkbox</li>
 *     <li>Sign in button</li>
 *     <li>Create account button (opens a RegisterDialog)</li>
 *     <li>Forgot password link</li>
 * </ul>
 *
 * The login process uses {@link AuthService} to authenticate existing users,
 * and account creation is handled via the {@link RegisterDialog}.
 */
public class LoginScreenFX extends Application {

    // --- UI components ---
    private final TextField usernameField = new TextField();
    private final PasswordField passwordHidden = new PasswordField();
    private final TextField passwordShown = new TextField(); // bound for show/hide
    private final CheckBox rememberMeCheckBox = new CheckBox("Remember me");
    private final CheckBox showPasswordCheckBox = new CheckBox("Show");
    private final Button signInButton = new Button("Sign in");
    private final Button createAccountButton = new Button("Create account");
    private final Hyperlink forgotPasswordLink = new Hyperlink("Forgot password?");

    // --- Authentication service ---
    public final AuthService auth = new AuthService();

    /**
     * Entry point for the JavaFX application.
     *
     * @param stage The primary stage provided by the JavaFX runtime.
     */
    @Override
    public void start(Stage stage) {
        stage.setTitle("PokéMath — Sign in");

        // Ensure the separate AUTH database/table exists
        try {
            AuthDatabase.ensureSchema();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,
                    "Auth DB init failed: " + e.getMessage()).showAndWait();
        }

        // Heading label
        Label heading = new Label("Welcome to PokéMath");
        heading.setFont(Font.font(26));

        // --- Password show/hide binding ---
        passwordShown.managedProperty().bind(showPasswordCheckBox.selectedProperty());
        passwordShown.visibleProperty().bind(showPasswordCheckBox.selectedProperty());
        passwordHidden.managedProperty().bind(showPasswordCheckBox.selectedProperty().not());
        passwordHidden.visibleProperty().bind(showPasswordCheckBox.selectedProperty().not());
        passwordShown.textProperty().bindBidirectional(passwordHidden.textProperty());

        // --- Layout grid ---
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(16));

        int r = 0;
        grid.add(heading, 0, r, 3, 1);

        r++;
        grid.add(new Label("Email / Username"), 0, r);
        grid.add(usernameField, 1, r);

        r++;
        grid.add(new Label("Password"), 0, r);
        grid.add(passwordHidden, 1, r);
        grid.add(passwordShown, 1, r); // stacked; visibility handled by binding

        r++;
        HBox toggles = new HBox(16, rememberMeCheckBox, showPasswordCheckBox);
        toggles.setAlignment(Pos.CENTER_LEFT);
        grid.add(toggles, 1, r);

        r++;
        HBox actions = new HBox(12, createAccountButton, signInButton);
        actions.setAlignment(Pos.CENTER_LEFT);
        grid.add(actions, 1, r);

        r++;
        grid.add(forgotPasswordLink, 1, r);

        // --- Wire button actions ---
        signInButton.setOnAction(e -> attemptLogin(stage));
        createAccountButton.setOnAction(e -> openRegisterDialog(stage));
        forgotPasswordLink.setOnAction(e ->
                new Alert(Alert.AlertType.INFORMATION,
                        "Ask your teacher to reset it.").showAndWait()
        );

        // --- Scene setup ---
        Scene scene = new Scene(grid, 520, 340);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Attempts to log in using the entered username and password.
     *
     * @param owner The parent stage, used for alerts.
     */
    private void attemptLogin(Stage owner) {
        String u = getUsername();
        String p = getPassword();
        try {
            if (auth.login(u, p)) {
                new Alert(Alert.AlertType.INFORMATION,
                        "Welcome, " + u + "!").showAndWait();
                // TODO: open your main game window here
            } else {
                Alert a = new Alert(Alert.AlertType.WARNING,
                        "Invalid username or password.");
                a.setHeaderText("Login failed");
                a.showAndWait();
            }
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR,
                    "Login error: " + ex.getMessage()).showAndWait();
        }
    }

    /**
     * Opens the registration dialog for creating a new account.
     *
     * @param owner The parent stage, used for centering the dialog.
     */
    private void openRegisterDialog(Stage owner) {
        RegisterDialog dialog = new RegisterDialog();
        dialog.initOwner(owner);

        dialog.showAndWait().ifPresent(result -> {
            try {
                auth.register(result.username, result.password,
                        result.email, result.gradeYearLevel);

                // Prefill login form with new account
                setUsername(result.username);
                setPassword(result.password);

                new Alert(Alert.AlertType.INFORMATION,
                        "Account created. You can sign in now.").showAndWait();
            } catch (IllegalArgumentException | IllegalStateException ex) {
                new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR,
                        "Registration error: " + ex.getMessage()).showAndWait();
            }
        });
    }

    // --- Getters/Setters for UI state ---

    /**  The username field text (trimmed). */
    public String getUsername() { return usernameField.getText().trim(); }

    /** @return The password entered, visible or hidden depending on checkbox state. */
    public String getPassword() {
        return isShowPasswordChecked() ? passwordShown.getText() : passwordHidden.getText();
    }

    /** true if Remember me is checked. */
    public boolean isRememberMeChecked() { return rememberMeCheckBox.isSelected(); }

    /** true i "Show password" is checked. */
    public boolean isShowPasswordChecked() { return showPasswordCheckBox.isSelected(); }

    /** Sets the username field text. */
    public void setUsername(String username) { usernameField.setText(username); }

    /** Sets the password in both hidden and shown fiEld. */
    public void setPassword(String password) {
        passwordHidden.setText(password);
        passwordShown.setText(password);
    }

    /** Sets the "Remember me" checkbox. */
    public void setRememberMeChecked(boolean b) { rememberMeCheckBox.setSelected(b); }

    /** Sets the "Show password" checkbox. */
    public void setShowPasswordChecked(boolean b) { showPasswordCheckBox.setSelected(b); }

    /**
     * Launches the JavaFX application
     *  args CLI args, unused.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
