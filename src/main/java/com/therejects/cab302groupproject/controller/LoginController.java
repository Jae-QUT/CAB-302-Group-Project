package com.therejects.cab302groupproject.controller;

import com.example.mon.app.User;
import com.therejects.cab302groupproject.MainMenuLauncher;
import com.therejects.cab302groupproject.Navigation.ScreenManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.scene.layout.StackPane;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * For View More Carousel functionality
 */
import javafx.animation.TranslateTransition;
import javafx.animation.ParallelTransition;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;


/**
 * A controller class responsible for managing the login screen of the application.
 * It handles the initialisation of the hero image, validates user credentials, and
 * provides interactivity for the login and "View More" actions.
 */
 public class LoginController {
    @FXML private ImageView heroImage;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMe;
    @FXML private StackPane infoOverlay;
    @FXML private ImageView slideImage;
    @FXML private VBox      slideContent;
    @FXML private Label     slideTitle;
    @FXML private Label     slideBody;
    @FXML private Button    slideCta;
    @FXML private HBox      dots;
    @FXML private StackPane carouselRoot;
    @FXML private Button prevBtn;
    @FXML private Button nextBtn;



    /**
     * Initialises the login screen by loading and displaying the hero image.
     */
    @FXML
    public void initialize() {
        var url = MainMenuLauncher.class.getResource("/images/MMLogin.png");
        if (url != null) {
            heroImage.setImage(new Image(url.toExternalForm()));
        } else {
            System.err.println("Missing /images/MMLogin.png on classpath. " +
                    "Put it under src/main/resources/images/ and rebuild.");
        }

        // ▼ Carousel setup (safe if overlay not visible yet)
        slides.add(new Slide(
                "Why Kids Love Math Monsters",
                "Catch cute monsters and level up with quick mini-games that build mental maths without the boredom.",
                "See Parent Benefits",
                "/images/MMLogin.png"
        ));
        slides.add(new Slide(
                "Why Parents Trust It",
                "Aligned with curriculum. No ads or open chat. Progress reports show accuracy and speed gains.",
                "Benefits of Practice",
                "/images/MMLogin.png"
        ));
        slides.add(new Slide(
                "Benefits of Practice",
                "Short daily sessions improve fluency in times tables, number sense, and word problems—boosting classroom confidence.",
                "What’s New",
                "/images/MMLogin.png"
        ));
        slides.add(new Slide(
                "What’s Coming Next",
                "Co-op boss battles, team quests, and Skill Trees for strategies like decomposition and estimation.",
                "Start Learning",
                "/images/MMLogin.png"
        ));

        // Build dots and initial slide
        rebuildDots();
        applySlide(0, false);

        // ----- Force dots location and layout -----
        Platform.runLater(() -> {
            if (slideImage != null) slideImage.setMouseTransparent(true);
            if (slideContent != null) slideContent.setPadding(new Insets(0, 56, 40, 56)); // keeps arrows clear

            if (dots != null && carouselRoot != null) {
                dots.setManaged(false);
                dots.setFillHeight(false);

                // define position function
                Runnable positionDots = () -> {
                    dots.applyCss();
                    dots.layout();
                    carouselRoot.applyCss();
                    carouselRoot.layout();

                    double w = carouselRoot.getWidth();
                    double h = carouselRoot.getHeight();
                    double dw = Math.max(dots.prefWidth(-1), dots.getWidth());
                    double dh = Math.max(dots.prefHeight(-1), dots.getHeight());

                    double x = (w - dw) / 2.0;
                    double y = h - dh + 80;
                    dots.relocate(x, y);
                };

                // run once immediately
                positionDots.run();

                // re-run when resized
                carouselRoot.widthProperty().addListener((o, a, b) -> positionDots.run());
                carouselRoot.heightProperty().addListener((o, a, b) -> positionDots.run());
            }
        });

        if (prevBtn != null) {
            prevBtn.setFocusTraversable(false);
            prevBtn.toFront();                 // ensure above background/dots
        }
        if (nextBtn != null) {
            nextBtn.setFocusTraversable(false);
            nextBtn.toFront();
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
     * Slides about: why you should play, why parents should encourage their children to sign up,
     * why you should practice every day, and new additions added to the game.
     */
    @FXML
    private void onViewMore() {
        infoOverlay.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), infoOverlay);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    @FXML
    private void onCloseOverlay() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(250), infoOverlay);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> infoOverlay.setVisible(false));
        fadeOut.play();
    }

    // ===== Carousel handlers =====
    @FXML
    private void onNext() {
        int next = (current + 1) % slides.size();
        showSlide(next, +1);
    }
    @FXML
    private void onPrev() {
        int prev = (current - 1 + slides.size()) % slides.size();
        showSlide(prev, -1);
    }
    @FXML private void onCarouselCta() {
        if (current < slides.size() - 1) onNext(); else onCloseOverlay();
    }

    private void showSlide(int index, int direction) {
        if (index < 0 || index >= slides.size()) return;

        // slide-out current (move in direction clicked)
        TranslateTransition outT = new TranslateTransition(Duration.millis(200), slideContent);
        outT.setFromX(0);
        outT.setToX(direction < 0 ? 20 : -20); // 🔄 swapped signs

        FadeTransition outF = new FadeTransition(Duration.millis(200), slideContent);
        outF.setFromValue(1);
        outF.setToValue(0);

        ParallelTransition out = new ParallelTransition(outT, outF);
        out.setOnFinished(e -> {
            applySlide(index, true);

            // new slide starts off-screen in opposite direction
            slideContent.setTranslateX(direction < 0 ? -20 : 20); // 🔄 opposite side entry

            // slide-in new (back to center)
            TranslateTransition inT = new TranslateTransition(Duration.millis(220), slideContent);
            inT.setFromX(slideContent.getTranslateX());
            inT.setToX(0);

            FadeTransition inF = new FadeTransition(Duration.millis(220), slideContent);
            inF.setFromValue(0);
            inF.setToValue(1);

            new ParallelTransition(inT, inF).play();
        });
        out.play();
    }

    private void applySlide(int index, boolean updateDots) {
        current = index;
        Slide s = slides.get(index);

        slideTitle.setText(s.title);
        slideBody.setText(s.body);
        slideCta.setText(s.cta != null ? s.cta : "Continue");

        if (s.imagePath != null) {
            var imgUrl = getClass().getResource(s.imagePath);
            slideImage.setImage(imgUrl != null ? new Image(imgUrl.toExternalForm()) : null);
            slideImage.setOpacity(0.18);
        } else {
            slideImage.setImage(null);
        }

        if (updateDots) refreshDots();
    }

    private void rebuildDots() {
        if (dots == null) return;
        dots.getChildren().clear();
        for (int i = 0; i < slides.size(); i++) {
            final int idx = i;
            Circle dot = new Circle(4);
            dot.getStyleClass().add("dot");
            dot.setFill(Color.rgb(11, 27, 58, 0.28));
            dot.setOnMouseClicked(e -> showSlide(idx, idx > current ? +1 : -1));
            dots.getChildren().add(dot);
        }
        refreshDots();
    }

    private void refreshDots() {
        if (dots == null) return;
        for (int i = 0; i < dots.getChildren().size(); i++) {
            Circle c = (Circle) dots.getChildren().get(i);
            c.setFill(i == current ? Color.web("#1f8fff") : Color.rgb(11,27,58,0.28));
        }
    }

    // ===== Register dialog =====

    @FXML
    private void onRegister() {
        Dialog<com.therejects.cab302groupproject.controller.LoginController.RegistrationData> dialog = new Dialog<>();
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
                return new com.therejects.cab302groupproject.controller.LoginController.RegistrationData(
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

        Optional<com.therejects.cab302groupproject.controller.LoginController.RegistrationData> result = dialog.showAndWait();
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

        /**
         * This is the storage when creating a new user. This is referenced throughout the registration process.
         * Is currently linked to the login that is not in use.
         * @param name User's name
         * @param email User's school email
         * @param username User's chosen display name
         * @param password User's password stored as a hash
         * @param studentNumber User's student number
         * @param classroomNumber User's assigned classroom
         * @param yearLevel User's grade level
         */
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

    /**
     * Class to handle the slide image in view more.
     */
    private static class Slide {
        final String title, body, cta, imagePath;
        Slide(String title, String body, String cta, String imagePath) {
            this.title = title; this.body = body; this.cta = cta; this.imagePath = imagePath;
        }
    }
    private final List<Slide> slides = new ArrayList<>();
    private int current = 0;
}
