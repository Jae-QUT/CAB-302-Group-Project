package com.therejects.cab302groupproject.controller;

import com.therejects.cab302groupproject.Navigation.ScreenManager;
import com.therejects.cab302groupproject.model.*;
import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Optional;

/**
 * JavaFX implementation of the login screen for the PokéMath / Math Monsters application.
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
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Math Monsters — Sign in");

        try {
            AuthDatabase.ensureSchema();
        } catch (Exception e) {
            showStyledAlert(Alert.AlertType.ERROR, "Auth DB init failed: " + e.getMessage());
        }

        // --- Heading ---
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

        // Input fields
        usernameField.setPromptText("Email / Username");
        passwordHidden.setPromptText("Password");
        passwordShown.setPromptText("Password");

        r++;
        grid.add(usernameField, 0, r, 2, 1);

        r++;
        grid.add(passwordHidden, 0, r, 2, 1);
        grid.add(passwordShown, 0, r, 2, 1);

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

        // --- Actions ---
        signInButton.setOnAction(e -> attemptLogin(stage));
        createAccountButton.setOnAction(e -> openRegisterDialog(stage));
        forgotPasswordLink.setOnAction(e -> handleForgotPassword(stage));

        // --- Background and scene ---
        Pane backgroundLayer = buildBackgroundLayer();

        StackPane gridWrapper = new StackPane(grid);
        gridWrapper.setAlignment(Pos.CENTER);
        grid.setMaxWidth(375);

        StackPane root = new StackPane(backgroundLayer, gridWrapper);
        root.getStyleClass().addAll("login-root", "app-root");
        /// --- Custom Exit Button ---///
        Button exitButton = new Button("✕");
        exitButton.setStyle("""
                 -fx-background-color: transparent;
                 -fx-text-fill: white;
                 -fx-font-size: 18px;
                 -fx-font-weight: bold;
                 -fx-cursor: hand;
        """);
        exitButton.setOnMouseEntered(e -> exitButton.setStyle("-fx-text-fill: #FF6F61; -fx-background-color: transparent; -fx-font-size: 18px; -fx-font-weight: bold; -fx-cursor: hand;"));
        exitButton.setOnMouseExited(e -> exitButton.setStyle("-fx-text-fill: white; -fx-background-color: transparent; -fx-font-size: 18px; -fx-font-weight: bold; -fx-cursor: hand;"));

        // Exit action
        exitButton.setOnAction(e -> stage.close());

        // Position the button top-right
        StackPane.setAlignment(exitButton, Pos.TOP_RIGHT);
        StackPane.setMargin(exitButton, new Insets(8, 10, 0, 0));
        root.getChildren().add(exitButton);




        Scene scene = new Scene(root, 520, 340);

        /// Hopefully sets the whole window to blue, removing the ugly Windows bars///
        stage.initStyle(StageStyle.TRANSPARENT);
        root.setOnMousePressed(e -> xOffset = e.getSceneX());
        root.setOnMouseDragged(e -> stage.setX(e.getScreenX() - xOffset));

        /// Allows the GUI to still be draggable, im sorry but Windows 11 has been the biggest scam since the doomsday preppers for y2k///
        root.setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });

        root.setOnMouseDragged(e -> {
            stage.setX(e.getScreenX() - xOffset);
            stage.setY(e.getScreenY() - yOffset);
        });


        var css = getClass().getResource("/ui/login.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.err.println("⚠ login.css not found in /ui/");
        }

        stage.setScene(scene);
        stage.setTitle("Math Monsters - Login");
        stage.show();
    }

    // =========================================================
    // ============ Forgot Password Workflow ===================
    // =========================================================
    private void handleForgotPassword(Stage stage) {
        // === Custom transparent alert replacement ===
        Dialog<ButtonType> choice = new Dialog<>();
        choice.initStyle(StageStyle.TRANSPARENT);
        choice.setTitle(null);
        choice.setHeaderText("Do you already have a reset token?");
        makeDialogDraggable(choice);

        // Blue rounded card
        DialogPane cpane = choice.getDialogPane();
        cpane.setBackground(new Background(
                new BackgroundFill(Color.web("#3BA8FF"), new CornerRadii(20), Insets.EMPTY)
        ));
        cpane.setBorder(new Border(new BorderStroke(Color.web("#3BA8FF"),
                BorderStrokeStyle.SOLID, new CornerRadii(20), BorderWidths.DEFAULT)));

        // Fully transparent window background
        cpane.getScene().setFill(Color.TRANSPARENT);

        // Clip to match rounded border
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(cpane.widthProperty());
        clip.heightProperty().bind(cpane.heightProperty());
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        cpane.setClip(clip);

        // Buttons
        ButtonType yesBtn = new ButtonType("Yes");
        ButtonType noBtn = new ButtonType("No");
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        cpane.getButtonTypes().setAll(yesBtn, noBtn, cancelBtn);

        // Optional — drag support
        cpane.setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });
        cpane.setOnMouseDragged(e -> {
            Stage dialogStage = (Stage) cpane.getScene().getWindow();
            dialogStage.setX(e.getScreenX() - xOffset);
            dialogStage.setY(e.getScreenY() - yOffset);
        });

        // Show and capture result
        Optional<ButtonType> resultOpt = choice.showAndWait();
        ButtonType result = resultOpt.orElse(cancelBtn);

        // === Step 2: Has Token ===
        if (result == yesBtn) {
            Dialog<String[]> resetDialog = new Dialog<>();
            resetDialog.initStyle(StageStyle.TRANSPARENT);
            resetDialog.setTitle(null);
            resetDialog.setHeaderText("Paste your token and set a new password");
            makeDialogDraggable(resetDialog);

            DialogPane paneReset = resetDialog.getDialogPane();
            paneReset.getScene().setFill(Color.TRANSPARENT);
            paneReset.setBackground(new Background(new BackgroundFill(Color.web("#3BA8FF"), new CornerRadii(20), Insets.EMPTY)));
            paneReset.setBorder(new Border(new BorderStroke(Color.web("#3BA8FF"),
                    BorderStrokeStyle.SOLID, new CornerRadii(20), BorderWidths.DEFAULT)));

            Rectangle clipReset = new Rectangle();
            clipReset.widthProperty().bind(paneReset.widthProperty());
            clipReset.heightProperty().bind(paneReset.heightProperty());
            clipReset.setArcWidth(20);
            clipReset.setArcHeight(20);
            paneReset.setClip(clipReset);

            TextField tokenField = new TextField();
            tokenField.setPromptText("Reset Token");
            PasswordField newPasswordField = new PasswordField();
            newPasswordField.setPromptText("New Password");

            GridPane tokenGrid = new GridPane();
            tokenGrid.setHgap(10);
            tokenGrid.setVgap(10);
            tokenGrid.setPadding(new Insets(20, 150, 10, 10));
            tokenGrid.add(new Label("Token:"), 0, 0);
            tokenGrid.add(tokenField, 1, 0);
            tokenGrid.add(new Label("New Password:"), 0, 1);
            tokenGrid.add(newPasswordField, 1, 1);
            paneReset.setContent(tokenGrid);

            ButtonType confirmBtn = new ButtonType("Reset Password", ButtonBar.ButtonData.OK_DONE);
            paneReset.getButtonTypes().addAll(confirmBtn, ButtonType.CANCEL);

            resetDialog.setResultConverter(btn ->
                    btn == confirmBtn ? new String[]{tokenField.getText().trim(), newPasswordField.getText()} : null
            );


            resetDialog.showAndWait().ifPresent(values -> {
                if (values[0].trim().isEmpty() || values[1].trim().isEmpty()) {
                    showStyledAlert(Alert.AlertType.WARNING, "Please enter both the token and a new password.");
                    return;
                }

                try {
                    auth.resetPassword(values[0], values[1]);
                    showStyledAlert(Alert.AlertType.INFORMATION, "Password successfully updated!");
                } catch (Exception ex) {
                    showStyledAlert(Alert.AlertType.ERROR, "Reset failed: " + ex.getMessage());
                }

            });

            return;
        }


        // === Step 3: Needs Token ===
        if (result == noBtn) {
            Dialog<String> requestDialog = new Dialog<>();
            requestDialog.initStyle(StageStyle.TRANSPARENT);
            requestDialog.setTitle(null);
            requestDialog.setHeaderText("Enter your registered email or username:");
            makeDialogDraggable(requestDialog);

            DialogPane paneEmail = requestDialog.getDialogPane();
            paneEmail.getScene().setFill(Color.TRANSPARENT);
            paneEmail.setBackground(new Background(new BackgroundFill(Color.web("#3BA8FF"), new CornerRadii(20), Insets.EMPTY)));
            paneEmail.setBorder(new Border(new BorderStroke(Color.web("#3BA8FF"),
                    BorderStrokeStyle.SOLID, new CornerRadii(20), BorderWidths.DEFAULT)));

            Rectangle clipEmail = new Rectangle();
            clipEmail.widthProperty().bind(paneEmail.widthProperty());
            clipEmail.heightProperty().bind(paneEmail.heightProperty());
            clipEmail.setArcWidth(20);
            clipEmail.setArcHeight(20);
            paneEmail.setClip(clipEmail);

            TextField accountField = new TextField();
            accountField.setPromptText("Email or Username");

            GridPane accountGrid = new GridPane();
            accountGrid.setHgap(10);
            accountGrid.setVgap(10);
            accountGrid.setPadding(new Insets(20, 150, 10, 10));
            accountGrid.add(new Label("Account:"), 0, 0);
            accountGrid.add(accountField, 1, 0);
            paneEmail.setContent(accountGrid);

            ButtonType sendBtn = new ButtonType("Send Reset Email", ButtonBar.ButtonData.OK_DONE);
            paneEmail.getButtonTypes().addAll(sendBtn, ButtonType.CANCEL);

            requestDialog.setResultConverter(btn -> btn == sendBtn ? accountField.getText().trim() : null);
            requestDialog.showAndWait().ifPresent(identifier -> {
                if (identifier == null || identifier.trim().isEmpty()) {
                    showStyledAlert(Alert.AlertType.WARNING,
                            "Please enter your email or username.");
                    return;
                }

                try {
                    String token = auth.generateResetToken(identifier.trim());
                    String email = auth.getEmailForUser(identifier.trim());

                    EmailService emailService = new EmailService();
                    emailService.sendPasswordResetEmail(email, token);

                    showStyledAlert(Alert.AlertType.INFORMATION,
                            "A password reset email has been sent to " + email +
                                    ".\nWhen you receive it, click 'Forgot password?' again and choose 'Yes'.");
                } catch (Exception ex) {
                    showStyledAlert(Alert.AlertType.ERROR, "Error: " + ex.getMessage());
                }
            });

        }
    }
    // =========================================================//
    // =============== Login + Register Flow ====================//
    // =========================================================//
    private void attemptLogin(Stage owner) {
        String u = getUsername();
        String p = getPassword();
        try {
            if (auth.login(u, p)) {
                UserDao dao = new UserDao();
                var opt = dao.findByUsername(u);
                User loggedIn = opt.get();
                User.setCurrentUser(loggedIn);
                showStyledAlert(Alert.AlertType.INFORMATION, "Welcome, " + u + "!");
                new ScreenManager(owner).navigateTo("MAIN_MENU");
            } else {
                showStyledAlert(Alert.AlertType.WARNING, "Invalid username or password.");
            }
        } catch (Exception ex) {
            showStyledAlert(Alert.AlertType.ERROR, "Login error: " + ex.getMessage());
        }
    }

    private void openRegisterDialog(Stage owner) {
        RegisterDialog dialog = new RegisterDialog();
        dialog.initStyle(StageStyle.TRANSPARENT);
        applyDialogTheme(dialog, "createaccount.css");
        makeDialogDraggable(dialog);

        dialog.initOwner(owner);
        dialog.showAndWait().ifPresent(result -> {
            try {
                auth.register(result.username, result.password,
                        result.email, result.gradeYearLevel, result.score);
                setUsername(result.username);
                setPassword(result.password);
                Dialog<Void> successDialog = new Dialog<>();
                makeDialogDraggable(successDialog);
                successDialog.initStyle(StageStyle.TRANSPARENT);
                DialogPane pane = successDialog.getDialogPane();
                pane.getScene().setFill(Color.TRANSPARENT);
                pane.setBackground(new Background(new BackgroundFill(Color.web("#3BA8FF"), new CornerRadii(20), Insets.EMPTY)));
                pane.setBorder(new Border(new BorderStroke(Color.web("#3BA8FF"),
                        BorderStrokeStyle.SOLID, new CornerRadii(20), BorderWidths.DEFAULT)));

                Rectangle clip = new Rectangle();
                clip.widthProperty().bind(pane.widthProperty());
                clip.heightProperty().bind(pane.heightProperty());
                clip.setArcWidth(20);
                clip.setArcHeight(20);
                pane.setClip(clip);

                pane.setContent(new Label("🎉 Account created successfully!\nYou can sign in now."));
                ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                pane.getButtonTypes().add(ok);
                successDialog.showAndWait();

            } catch (Exception ex) {
                showStyledAlert(Alert.AlertType.ERROR, "Registration error: " + ex.getMessage());
            }
        });
    }

    // =========================================================
    // =============== Utility Methods ==========================
    // =========================================================
    private void showStyledAlert(Alert.AlertType type, String msg) {
        Dialog<Void> alert = new Dialog<>();
        alert.initStyle(StageStyle.TRANSPARENT);
        alert.setTitle(null);
        alert.setHeaderText(null);

        DialogPane pane = alert.getDialogPane();
        pane.getScene().setFill(Color.TRANSPARENT);

      /// colour based alert styling///
        String bgColor = switch (type) {
            case INFORMATION -> "#3BA8FF"; // blue
            case WARNING -> "#FFD54F";     // yellow
            case ERROR -> "#FF6F61";       // red
            default -> "#3BA8FF";
        };

        makeDialogDraggable(alert);

        /// Clip to remove white corners///
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(pane.widthProperty());
        clip.heightProperty().bind(pane.heightProperty());
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        pane.setClip(clip);

        /// Message text///
        Label content = new Label(msg);
        content.setWrapText(true);
        content.setPadding(new Insets(20, 40, 20, 40));
        content.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        pane.setContent(content);

        /// OK button///
        ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        pane.getButtonTypes().add(ok);

        alert.showAndWait();
    }
    private void applyDialogTheme(Dialog<?> dialog, String cssName) {
        try {
            var cssUrl = getClass().getResource("/ui/" + cssName);
            if (cssUrl != null) {
                dialog.getDialogPane().getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("⚠ Missing CSS: /ui/" + cssName);
            }
        } catch (Exception e) {
            System.err.println("⚠ Failed to apply CSS theme " + cssName + ": " + e.getMessage());
        }
    }
    private void makeDialogDraggable(Dialog<?> dialog) {
        DialogPane pane = dialog.getDialogPane();

        /// Allow dragging from anywhere on the dialog pane///
        final double[] offset = new double[2];

        pane.setOnMousePressed(e -> {
            offset[0] = e.getSceneX();
            offset[1] = e.getSceneY();
        });

        pane.setOnMouseDragged(e -> {
            Stage s = (Stage) pane.getScene().getWindow();
            s.setX(e.getScreenX() - offset[0]);
            s.setY(e.getScreenY() - offset[1]);
        });
    }


    private Pane buildBackgroundLayer() {
        Pane backgroundLayer = new Pane();
        backgroundLayer.setMouseTransparent(true);

        ImageView background = new ImageView(new Image(
                getClass().getResource("/images/Sprites/Background.png").toExternalForm()
        ));
        background.setPreserveRatio(false);
        background.setFitWidth(530);
        background.setFitHeight(350);
        background.setLayoutX(-32);
        background.setLayoutY(-32);
        background.setSmooth(true);

        ImageView batmon = new ImageView(new Image(
                getClass().getResource("/images/Sprites/Batmon.png").toExternalForm()
        ));
        batmon.setFitWidth(90);
        batmon.setPreserveRatio(true);
        batmon.setLayoutX(-10);
        batmon.setLayoutY(220);

        ImageView sharkle = new ImageView(new Image(
                getClass().getResource("/images/Sprites/Sharkle.png").toExternalForm()
        ));
        sharkle.setFitWidth(120);
        sharkle.setPreserveRatio(true);
        sharkle.setLayoutX(365);
        sharkle.setLayoutY(190);

        ImageView hawtosaur = new ImageView(new Image(
                getClass().getResource("/images/Sprites/Hawtosaur.png").toExternalForm()
        ));
        hawtosaur.setFitWidth(80);
        hawtosaur.setPreserveRatio(true);
        hawtosaur.setLayoutX(375);
        hawtosaur.setLayoutY(15);

        backgroundLayer.getChildren().addAll(background, batmon, sharkle, hawtosaur);

        // Bobbing animations
        createBobbing(batmon, 2.0, -10).play();
        createBobbing(sharkle, 2.3, -8).play();
        createBobbing(hawtosaur, 1.8, -12).play();

        return backgroundLayer;
    }

    private Timeline createBobbing(ImageView node, double seconds, double height) {
        Timeline t = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(node.translateYProperty(), 0)),
                new KeyFrame(Duration.seconds(seconds), new KeyValue(node.translateYProperty(), height))
        );
        t.setAutoReverse(true);
        t.setCycleCount(Animation.INDEFINITE);
        return t;
    }

    // --- Getters/Setters ---
    public String getUsername() { return usernameField.getText().trim(); }
    public String getPassword() { return isShowPasswordChecked() ? passwordShown.getText() : passwordHidden.getText(); }
    public boolean isRememberMeChecked() { return rememberMeCheckBox.isSelected(); }
    public boolean isShowPasswordChecked() { return showPasswordCheckBox.isSelected(); }
    public void setUsername(String username) { usernameField.setText(username); }
    public void setPassword(String password) { passwordHidden.setText(password); passwordShown.setText(password); }

    public static void main(String[] args) { launch(args); }
}
