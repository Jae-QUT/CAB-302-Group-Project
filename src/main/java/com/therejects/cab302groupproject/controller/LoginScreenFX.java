package com.therejects.cab302groupproject.controller;
import com.therejects.cab302groupproject.Navigation.ScreenManager;
import com.therejects.cab302groupproject.model.User;
import com.therejects.cab302groupproject.model.*;
import com.therejects.cab302groupproject.model.UserDao;
import com.therejects.cab302groupproject.model.AuthService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

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
    private final AuthService auth = new AuthService();
    private final ScoringSystem ScoringSystem = new ScoringSystem();

    // --- Authentication service ---
    //private final AuthService auth = new AuthService();

    /**
     * Entry point for the JavaFX application.
     *
     * @param stage The primary stage provided by the JavaFX runtime.
     */
    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Math Monsters — Sign in");

        // Ensure the separate AUTH database/table exists
        try {
            AuthDatabase.ensureSchema();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,
                    "Auth DB init failed: " + e.getMessage()).showAndWait();
        }

        // Heading label
        Label heading = new Label("Welcome to Math Monsters!");
        heading.setFont(Font.font(26));
        heading.getStyleClass().add("heading");


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
        grid.getStyleClass().add("grid-pane");

        int r = 0;
        grid.add(heading, 0, r, 3, 1);

        // --- Input fields with placeholder text ---
        usernameField.setPromptText("Email / Username");
        passwordHidden.setPromptText("Password");
        passwordShown.setPromptText("Password");

        // Adjust layout: no labels, just fields centered
        r++;
        grid.add(usernameField, 0, r, 2, 1);

        r++;
        grid.add(passwordHidden, 0, r, 2, 1);
        grid.add(passwordShown, 0, r, 2, 1); // stacked, managed by binding


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
        // --- Static Background Layer ---
        Pane backgroundLayer = new Pane();
        backgroundLayer.setMouseTransparent(true);

        // --- Background image (fills the entire window) ---
        ImageView background = new ImageView(new Image(
                getClass().getResource("/images/Sprites/Background.png").toExternalForm()
        ));

        // Fill the full scene, oversizing slightly to eliminate blue edges
        background.setPreserveRatio(false);
        background.setFitWidth(530);   // +10px to ensure full bleed
        background.setFitHeight(350);  // +10px to cover top bar
        background.setLayoutX(-32);     // offset left
        background.setLayoutY(-32);     // offset up
        background.setSmooth(true);

        backgroundLayer.getChildren().add(background);


        // --- Monster Sprites ---

        // Bottom-left: Batmon
        ImageView Batmon = new ImageView(new Image(
                getClass().getResource("/images/Sprites/Batmon.png").toExternalForm()
        ));
        Batmon.setFitWidth(90);
        Batmon.setPreserveRatio(true);
        Batmon.setLayoutX(-10);
        Batmon.setLayoutY(220);

        // Bottom-right: Sharkle
        ImageView Sharkle = new ImageView(new Image(
                getClass().getResource("/images/Sprites/Sharkle.png").toExternalForm()
        ));
        Sharkle.setFitWidth(120);
        Sharkle.setPreserveRatio(true);
        Sharkle.setLayoutX(365);
        Sharkle.setLayoutY(190);

        // Top-right: Hawtosaur
        ImageView Hawtosaur = new ImageView(new Image(
                getClass().getResource("/images/Sprites/Hawtosaur.png").toExternalForm()
        ));
        Hawtosaur.setFitWidth(80);
        Hawtosaur.setPreserveRatio(true);
        Hawtosaur.setLayoutX(375);
        Hawtosaur.setLayoutY(15);

        // Optional: soft drop shadow to separate from background
        Batmon.setEffect(new javafx.scene.effect.DropShadow(10, javafx.scene.paint.Color.rgb(0,0,0,0.4)));
        Sharkle.setEffect(new javafx.scene.effect.DropShadow(10, javafx.scene.paint.Color.rgb(0,0,0,0.4)));
        Hawtosaur.setEffect(new javafx.scene.effect.DropShadow(10, javafx.scene.paint.Color.rgb(0,0,0,0.4)));

        backgroundLayer.getChildren().addAll(Batmon, Sharkle, Hawtosaur);

        // --- Bobbing animations ---
        Timeline bob1 = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(Batmon.translateYProperty(), 0)),
                new KeyFrame(Duration.seconds(2), new KeyValue(Batmon.translateYProperty(), -10))
        );
        bob1.setAutoReverse(true);
        bob1.setCycleCount(Animation.INDEFINITE);
        bob1.play();

        Timeline bob2 = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(Sharkle.translateYProperty(), 0)),
                new KeyFrame(Duration.seconds(2.3), new KeyValue(Sharkle.translateYProperty(), -8))
        );
        bob2.setAutoReverse(true);
        bob2.setCycleCount(Animation.INDEFINITE);
        bob2.play();

        Timeline bob3 = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(Hawtosaur.translateYProperty(), 0)),
                new KeyFrame(Duration.seconds(1.8), new KeyValue(Hawtosaur.translateYProperty(), -12))
        );
        bob3.setAutoReverse(true);
        bob3.setCycleCount(Animation.INDEFINITE);
        bob3.play();



        // --- Layer everything together ---
        // Create a wrapper to center the login grid
        StackPane gridWrapper = new StackPane(grid);
        gridWrapper.setAlignment(Pos.CENTER); // centers the grid
        gridWrapper.setPadding(new Insets(0, 0, 0, 0));
        grid.setMaxWidth(375);
        StackPane.setAlignment(gridWrapper, Pos.CENTER);//

        // Combine background + centered content
        StackPane root = new StackPane(backgroundLayer, gridWrapper);

        root.getStyleClass().addAll("login-root", "app-root");

        Scene scene = new Scene(root, 520, 340);
        scene.getStylesheets().add(getClass().getResource("/ui/login.css").toExternalForm());

        root.getStyleClass().addAll("login-root", "app-root");

        stage.setScene(scene);
        stage.setTitle("Math Monsters - Login");
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
                UserDao dao = new UserDao();
                var opt = dao.findByUsername(u);
                User loggedIn = opt.get();
                User.setCurrentUser(loggedIn);
                new Alert(Alert.AlertType.INFORMATION,
                        "Welcome, " + u + "!").showAndWait();
                ScreenManager sm = new ScreenManager(owner);
                sm.navigateTo("MAIN_MENU");
                owner.show();
                owner.setTitle("Main Menu");


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
                        result.email, result.gradeYearLevel, result.score);

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
    public void setUsername(String username) {
        usernameField.setText(username);
    }

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
     * Launches the JavaFX application.
     *
     *  args CLI args, unused.
     */
    public static void main(String[] args) {
        launch(args);
    }

}
