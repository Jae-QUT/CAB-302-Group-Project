package com.therejects.cab302groupproject.controller;

import com.therejects.cab302groupproject.MainMenuLauncher;
import com.therejects.cab302groupproject.Navigation.ScreenManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.regex.Pattern;

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
        // This Navigates to the Main Scene using Screen Manager
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
    // ===== Register dialog =====

    @FXML
    private void onRegister() {
        Dialog<com.therejects.cab302groupproject.LoginController.RegistrationData> dialog = new Dialog<>();
        dialog.setTitle("Create account");
        dialog.setHeaderText("Enter your details");

        ButtonType createBtnType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(16, 20, 12, 20));

        TextField nameField = new TextField();
        nameField.setPromptText("Full name");

        TextField emailField = new TextField();
        emailField.setPromptText("name@student.qut.edu.au");

        TextField username = new TextField();
        username.setPromptText("username");

        PasswordField password = new PasswordField();
        password.setPromptText("Password (min 6 chars)");

        TextField studentNo = new TextField();
        studentNo.setPromptText("e.g., 123456");
        enforceNumeric(studentNo);

        TextField classroomNo = new TextField();
        classroomNo.setPromptText("Classroom number");
        enforceNumeric(classroomNo);

        ComboBox<Integer> yearLevel = new ComboBox<>();
        yearLevel.getItems().addAll(1,2,3,4,5,6,7,8,9,10,11,12);
        yearLevel.setPromptText("Year level");

        int r = 0;
        grid.add(new Label("Name"),              0, r); grid.add(nameField,  1, r++);
        grid.add(new Label("Student Email"),     0, r); grid.add(emailField, 1, r++);
        grid.add(new Label("Username"),          0, r); grid.add(username,   1, r++);
        grid.add(new Label("Password"),          0, r); grid.add(password,   1, r++);
        grid.add(new Label("Student number"),    0, r); grid.add(studentNo,  1, r++);
        grid.add(new Label("Classroom number"),  0, r); grid.add(classroomNo,1, r++);
        grid.add(new Label("Year level"),        0, r); grid.add(yearLevel,  1, r++);

        dialog.getDialogPane().setContent(grid);

        // Disable Create until valid
        Node createBtn = dialog.getDialogPane().lookupButton(createBtnType);
        Runnable validate = () -> {
            boolean ok = !nameField.getText().isBlank()
                    && isEmail(emailField.getText())
                    && !username.getText().isBlank()
                    && password.getText().length() >= 6
                    && !studentNo.getText().isBlank()
                    && !classroomNo.getText().isBlank()
                    && yearLevel.getValue() != null;
            createBtn.setDisable(!ok);
        };
        createBtn.setDisable(true);
        nameField.textProperty().addListener((o, a, b) -> validate.run());
        emailField.textProperty().addListener((o, a, b) -> validate.run());
        username.textProperty().addListener((o, a, b) -> validate.run());
        password.textProperty().addListener((o, a, b) -> validate.run());
        studentNo.textProperty().addListener((o, a, b) -> validate.run());
        classroomNo.textProperty().addListener((o, a, b) -> validate.run());
        yearLevel.valueProperty().addListener((o, a, b) -> validate.run());
        validate.run();

        dialog.setResultConverter(btn -> {
            if (btn == createBtnType) {
                return new com.therejects.cab302groupproject.LoginController.RegistrationData(
                        nameField.getText().trim(),
                        emailField.getText().trim().toLowerCase(),
                        username.getText().trim(),
                        password.getText(),
                        studentNo.getText().trim(),
                        classroomNo.getText().trim(),
                        yearLevel.getValue()
                );
            }
            return null;
        });

        Optional<com.therejects.cab302groupproject.LoginController.RegistrationData> result = dialog.showAndWait();
        result.ifPresent(data -> {
            // TODO: call your real registration service here
            System.out.println("Registering: " + data);

            // Optional UX: fill the login form & focus password
            usernameField.setText(data.username);
            passwordField.requestFocus();
            new Alert(Alert.AlertType.INFORMATION, "Account created. Please log in.").showAndWait();
        });
    }

    // ===== helpers =====

    private static void enforceNumeric(TextField tf) {
        tf.textProperty().addListener((obs, old, val) -> {
            if (val != null && !val.matches("\\d*")) {
                tf.setText(val.replaceAll("\\D", ""));
            }
        });
    }

    private static final Pattern EMAIL_RX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);

    private static boolean isEmail(String s) {
        return s != null && EMAIL_RX.matcher(s.trim()).matches();
    }

    /** Simple DTO for registration results. */
    public static class RegistrationData {
        public final String name, email, username, password, studentNumber, classroomNumber;
        public final Integer yearLevel;

        public RegistrationData(String name, String email, String username, String password,
                                String studentNumber, String classroomNumber, Integer yearLevel) {
            this.name = name;
            this.email = email;
            this.username = username;
            this.password = password;
            this.studentNumber = studentNumber;
            this.classroomNumber = classroomNumber;
            this.yearLevel = yearLevel;
        }

        @Override public String toString() {
            return "RegistrationData{" +
                    "name='" + name + '\'' +
                    ", email='" + email + '\'' +
                    ", username='" + username + '\'' +
                    ", studentNumber='" + studentNumber + '\'' +
                    ", classroomNumber='" + classroomNumber + '\'' +
                    ", yearLevel=" + yearLevel +
                    '}';
        }
    }
}
