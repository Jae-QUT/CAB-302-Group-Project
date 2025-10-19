package com.therejects.cab302groupproject.controller;

import com.example.mon.app.LoginScreenFX;
import com.example.mon.app.User;
import com.therejects.cab302groupproject.Navigation.ScreenManager;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

/**
 * Controller for the Main Menu scene.
 * <p>
 * Responsibilities:
 * <ul>
 *     <li>Initialize the background image and spawn five roaming monster sprites behind the UI.</li>
 *     <li>Handle left-aligned menu button actions (Play, Leaderboard, Player Profile, Logout).</li>
 *     <li>Provide ESC-to-Logout behavior and integrate with the app's screen navigation.</li>
 * </ul>
 * <p>
 * This controller adds only visual polish (background + animated sprites) and does not introduce
 * any new logical features or dependencies that increase team complexity.
 */
public class MainMenuController {

    private Consumer<String> navigator;
    private ScreenManager screenManager;

    // ----------- FXML-injected nodes -----------
    @FXML private StackPane root;
    @FXML private Button btnPlay, btnLeaderboard, btnChangeTeam, btnLogout;
    @FXML private ImageView bgImage;
    @FXML private Pane monsterLayer;

    // ----------- Roaming sprite state -----------
    private final List<ImageView> monsters = new ArrayList<>();
    private final List<double[]> velocities = new ArrayList<>();
    private AnimationTimer timer;
    private long lastNs = 0L;

    /**
     * Injects the navigation used by this controller to change screens. The navigator is a reference to the 'navigateTo'
     * method in the screenManager which allows this controller to trigger screen changes.
     *
     * @param navigator Is the direct reference to loadScreen in the ScreenManager. It gets the screen related to the
     *                  button and sends the user there
     */
    public void setNavigator(Consumer<String> navigator) {
        this.navigator = navigator;
    }

    /**
     * JavaFX lifecycle hook. Sets up focus/ESC behavior, loads the background image, spawns
     * the five sprites, and starts the wander animation. Also binds the background image to scene size.
     */
    @FXML
    private void initialize() {
        // UX: focus first actionable button
        Platform.runLater(() -> btnPlay.requestFocus());

        // ESC → Logout
        root.setOnKeyPressed(e -> {
            if (Objects.requireNonNull(e.getCode()) == KeyCode.ESCAPE) onLogout();
        });

        // Visual boot
        loadBackground();
        spawnMonsters();
        startWander();

        // Keep background sized with scene
        root.sceneProperty().addListener((obs, oldS, newS) -> {
            if (newS != null) {
                bgImage.fitWidthProperty().bind(newS.widthProperty());
                bgImage.fitHeightProperty().bind(newS.heightProperty());
            }
        });
    }

    // =====================================================================
    // Visual helpers: background + sprite spawning + animation
    // =====================================================================

    /**
     * Loads the pixel-fence background image and configures it to fill the scene.
     * <p>Update the resource path if your asset differs.</p>
     */
    private void loadBackground() {
        var url = getClass().getResource("/images/pixel_fence.png"); // change if needed
        if (url != null) {
            bgImage.setImage(new Image(url.toExternalForm(), true));
            bgImage.setPreserveRatio(false);
        } else {
            System.err.println("Fence image not found at /images/pixel_fence.png");
        }
    }

    /**
     * Creates five monster sprites from {@code /images/Sprites/*.png}, gives them subtle opacity and blur,
     * randomizes their scale and velocity, and adds them to the {@code monsterLayer}.
     * <p>File names are case-sensitive on macOS/Linux; ensure they exactly match.</p>
     */
    private void spawnMonsters() {
        final String base = "/images/Sprites/";
        final String[] files = {
                "Anqchor.png",
                "Batmon.png",
                "Hawtosaur.png",
                "Sharkle.png",
                "Zabird.png"
        };

        for (String f : files) {
            var url = getClass().getResource(base + f);
            if (url == null) {
                System.err.println("Sprite not found: " + base + f);
                continue;
            }

            ImageView m = new ImageView(new Image(url.toExternalForm(), true));
            m.setPreserveRatio(true);
            m.setOpacity(rand(0.18, 0.28));          // keep background feel
            m.setEffect(new GaussianBlur(1.8));
            m.setFitWidth(120 * rand(0.60, 1.15));   // scale variety

            monsterLayer.getChildren().add(m);
            monsters.add(m);

            double vx = rand(20, 55) * (Math.random() < 0.5 ? -1 : 1);
            double vy = rand(8, 20)  * (Math.random() < 0.5 ? -1 : 1);
            velocities.add(new double[]{vx, vy});

            // Face toward movement
            if (vx < 0) m.getTransforms().add(new Scale(-1, 1, 0, 0));
        }

        // Place once bounds are known
        monsterLayer.layoutBoundsProperty().addListener((o, ob, b) -> placeMonstersRandomly(b));
        if (monsterLayer.getLayoutBounds().getWidth() > 0) {
            placeMonstersRandomly(monsterLayer.getLayoutBounds());
        }
    }

    /**
     * Randomly places sprites within a central vertical band so they remain behind the main menu UI.
     *
     * @param b the current layout bounds of the {@code monsterLayer}
     */
    private void placeMonstersRandomly(Bounds b) {
        double w = b.getWidth(), h = b.getHeight();
        if (w <= 0 || h <= 0) return;

        for (Node n : monsters) {
            ImageView m = (ImageView) n;
            double mw = m.getBoundsInParent().getWidth();
            double mh = m.getBoundsInParent().getHeight();

            double x = rand(0, Math.max(0, w - mw));
            double y = rand(h * 0.18, h * 0.78 - mh);
            m.setLayoutX(x);
            m.setLayoutY(y);
        }
    }

    /**
     * Starts the per-frame wander animation. Sprites wrap horizontally and softly bounce within a vertical band.
     * Speeds gently drift over time for a “living” background feel.
     */
    private void startWander() {
        timer = new AnimationTimer() {
            @Override public void handle(long now) {
                if (lastNs == 0L) { lastNs = now; return; }
                double dt = (now - lastNs) / 1_000_000_000.0;
                lastNs = now;

                Bounds b = monsterLayer.getLayoutBounds();
                double w = b.getWidth(), h = b.getHeight();

                for (int i = 0; i < monsters.size(); i++) {
                    ImageView m = monsters.get(i);
                    double[] v = velocities.get(i);
                    double vx = v[0], vy = v[1];

                    double x = m.getLayoutX() + vx * dt;
                    double y = m.getLayoutY() + vy * dt;

                    double mw = m.getBoundsInParent().getWidth();
                    double mh = m.getBoundsInParent().getHeight();

                    // Horizontal wrap
                    if (x > w)       x = -mw;
                    if (x + mw < 0)  x =  w;

                    // Vertical band bounce
                    double top = h * 0.18, bottom = h * 0.80 - mh;
                    if (y > bottom) { y = bottom; vy = -Math.abs(vy); }
                    if (y < top)    { y = top;    vy =  Math.abs(vy); }

                    m.setLayoutX(x);
                    m.setLayoutY(y);

                    // Face toward movement
                    m.setScaleX(vx < 0 ? -Math.abs(m.getScaleX()) : Math.abs(m.getScaleX()));

                    // Tiny random drift for life
                    if (Math.random() < 0.002) {
                        vx = clamp(vx + rand(-8, 8), -65, 65);
                        vy = clamp(vy + rand(-5, 5), -25, 25);
                    }
                    v[0] = vx; v[1] = vy;
                }
            }
        };
        timer.start();
    }

    /**
     * Uniform random helper in the range [a, b).
     *
     * @param a lower inclusive bound
     * @param b upper exclusive bound
     * @return random double in [a, b)
     */
    private static double rand(double a, double b) {
        return ThreadLocalRandom.current().nextDouble(a, b);
    }

    /**
     * Clamps a value between {@code lo} and {@code hi}.
     *
     * @param v  value
     * @param lo lower bound
     * @param hi upper bound
     * @return clamped value
     */
    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    // =====================================================================
    // Button handlers & navigation
    // =====================================================================

    /**
     * Navigates to the Play screen.
     */
    @FXML private void onPlay() { navigate("PLAY"); }

    /**
     * Navigates to the Leaderboard screen.
     */
    @FXML private void onLeaderboard() { navigate("LEADERBOARD"); }

    /**
     * Navigates to the Player Profile screen.
     */
    @FXML private void onPlayerProfile() { navigate("PLAYER_PROFILE"); }

    /**
     * Convenience handler to navigate back to Main Menu from subviews (kept for parity).
     * Currently routes to Leaderboard as per original code.
     */
    @FXML private void onBackToMainMenu() { navigate("LEADERBOARD"); }

    /**
     * Logs the user out by clearing the current user and reusing the same Stage to show the Login screen.
     * If an error occurs, it is printed to stderr.
     */
    @FXML private void onLogout() {
        try {
            User.setCurrentUser(null);
            javafx.stage.Stage stage = (javafx.stage.Stage) root.getScene().getWindow();
            LoginScreenFX login = new LoginScreenFX();
            login.start(stage); // reuse stage
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Performs a navigation action using the injected {@link #navigator}. If the navigator has not been set,
     * a warning is printed to stderr.
     *
     * @param screenId the logical screen identifier understood by the ScreenManager
     */
    private void navigate(String screenId) {
        if (navigator != null) {
            navigator.accept(screenId);
        } else {
            System.err.println("Navigator not set for: " + screenId);
        }
    }
}
