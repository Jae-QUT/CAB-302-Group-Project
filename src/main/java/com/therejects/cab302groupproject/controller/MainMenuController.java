package com.therejects.cab302groupproject.controller;

import com.example.mon.app.LoginScreenFX;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.util.Duration;

import java.util.*;
import java.util.function.Consumer;

/**
 * Main menu controller for Math Monsters.
 * Displays the animated monster field background and handles navigation
 * between the main menu options (Play, Leaderboard, Profile, etc.).
 */
public class MainMenuController {

    // ====== Existing Fields ======
    private Consumer<String> navigator;

    @FXML private StackPane root;
    @FXML private Button btnPlay, btnLeaderboard, btnChangeTeam, btnLogout;

    // ====== New Fields for World Layer ======
    @FXML private Pane worldLayer; // defined in FXML
    private final List<Sprite> activeSprites = new ArrayList<>();
    private final Random rng = new Random();

    // ====== Lifecycle ======
    @FXML
    private void initialize() {
        Platform.runLater(() -> btnPlay.requestFocus());

        // Keyboard shortcut
        root.setOnKeyPressed(e -> {
            if (Objects.requireNonNull(e.getCode()) == KeyCode.ESCAPE) {
                onLogout(); // ESC to logout
            }
        });

        // Initialize background world
        Platform.runLater(() -> {
            if (worldLayer != null) {
                buildField();
                addFence();
                spawnDemoMonsters(); // stub until DB link
            }
        });
    }

    /**
     * Injects the navigation function used to change scenes.
     * @param navigator Function reference to ScreenManager.navigateTo(String)
     */
    public void setNavigator(Consumer<String> navigator) {
        this.navigator = navigator;
    }

    // ====== Navigation Actions ======
    @FXML private void onPlay()          { navigate("PLAY"); }
    @FXML private void onLeaderboard()   { navigate("LEADERBOARD"); }
    @FXML private void onPlayerProfile() { navigate("PLAYER_PROFILE"); }

    @FXML private void onLogout() {
        try {
            javafx.stage.Stage stage = (javafx.stage.Stage) root.getScene().getWindow();
            new LoginScreenFX().start(stage); // reuses same stage
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Optional new menu buttons (add to FXML if used)
    @FXML private void onTutorial() { navigate("TUTORIAL"); }
    @FXML private void onSettings() { navigate("SETTINGS"); }
    @FXML private void onCredits()  { navigate("CREDITS"); }
    @FXML private void onExit()     { Platform.exit(); }

    private void navigate(String screenId) {
        if (navigator != null) navigator.accept(screenId);
        else System.err.println("Navigator not set for: " + screenId);
    }

    // ====== World & Animation ======
    private void buildField() {
        var url = Objects.requireNonNull(
                getClass().getResource("/images/grass.png"),
                "Missing /images/grass.png"
        );

        // load at native size (no forced 64×64)
        Image bg = new Image(url.toExternalForm(), 0, 0, true, false);

        // make worldLayer follow the window size
        worldLayer.prefWidthProperty().bind(root.widthProperty());
        worldLayer.prefHeightProperty().bind(root.heightProperty());

        // scale the image to cover the pane; no repeat
        var size = new javafx.scene.layout.BackgroundSize(
                100, 100, true, true, false, true   // width/height as %, cover=true
        );

        worldLayer.setBackground(new javafx.scene.layout.Background(
                new javafx.scene.layout.BackgroundImage(
                        bg,
                        javafx.scene.layout.BackgroundRepeat.NO_REPEAT,
                        javafx.scene.layout.BackgroundRepeat.NO_REPEAT,
                        javafx.scene.layout.BackgroundPosition.CENTER,
                        size
                )));
    }


    private void addFence() {
        Image fence = new Image(Objects.requireNonNull(
                getClass().getResource("/art/tiles/fence.png")).toExternalForm());
        double y = 520;
        for (int x = 0; x < worldLayer.getPrefWidth(); x += fence.getWidth()) {
            ImageView iv = new ImageView(fence);
            iv.setSmooth(false);
            iv.setLayoutX(x);
            iv.setLayoutY(y);
            worldLayer.getChildren().add(iv);
        }
    }

    /** Temporary method to show animated monsters until DB integration. */
    private void spawnDemoMonsters() {
        for (int i = 0; i < 3; i++) {
            Sprite s = createSprite("/art/monsters/slime_sheet.png", 16, 16, 4, 8);
            activeSprites.add(s);
            worldLayer.getChildren().add(s.view);
            wander(s);
        }
    }

    private Sprite createSprite(String spriteSheet, int frameW, int frameH, int framesPerRow, int totalFrames) {
        Image sheet = new Image(Objects.requireNonNull(getClass().getResource(spriteSheet)).toExternalForm());
        ImageView view = new ImageView(sheet);
        view.setSmooth(false);
        view.setViewport(new Rectangle2D(0, 0, frameW, frameH));
        view.setScaleX(3); view.setScaleY(3);
        view.setLayoutX(100 + rng.nextInt(800));
        view.setLayoutY(360 + rng.nextInt(120));
        Sprite s = new Sprite(view, frameW, frameH, framesPerRow, totalFrames, 8);
        s.play();
        return s;
    }

    private void wander(Sprite s) {
        double startX = s.view.getLayoutX();
        double endX = Math.max(60, Math.min(startX + (rng.nextBoolean() ? 200 : -200), 1000));
        TranslateTransition t = new TranslateTransition(Duration.seconds(4 + rng.nextDouble() * 2), s.view);
        t.setFromX(0);
        t.setToX(endX - startX);
        t.setAutoReverse(true);
        t.setCycleCount(Animation.INDEFINITE);
        t.setInterpolator(Interpolator.EASE_BOTH);
        t.play();

        // Flip sprite facing direction
        t.currentTimeProperty().addListener((obs, o, n) -> {
            double progress = n.toMillis() / t.getDuration().toMillis();
            boolean goingRight = progress < 0.5;
            s.view.setScaleX(goingRight ? 3 : -3);
        });
    }

    /** Small helper class for sprite animation. */
    private static final class Sprite {
        final ImageView view;
        private final int fw, fh, framesPerRow, totalFrames;
        private final Timeline timeline;
        private int frame = 0;

        Sprite(ImageView view, int fw, int fh, int framesPerRow, int totalFrames, int fps) {
            this.view = view;
            this.fw = fw; this.fh = fh;
            this.framesPerRow = framesPerRow; this.totalFrames = totalFrames;
            this.timeline = new Timeline(new KeyFrame(Duration.millis(1000.0 / fps), e -> step()));
            this.timeline.setCycleCount(Animation.INDEFINITE);
        }

        private void step() {
            int col = frame % framesPerRow;
            int row = frame / framesPerRow;
            view.setViewport(new Rectangle2D(col * fw, row * fh, fw, fh));
            frame = (frame + 1) % totalFrames;
        }

        void play() { timeline.play(); }
        void stop() { timeline.stop(); }
    }
}
