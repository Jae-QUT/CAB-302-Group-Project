package com.therejects.cab302groupproject.controller;

import com.example.mon.app.LoginScreenFX;
import com.example.mon.app.User;
import com.therejects.cab302groupproject.Navigation.ScreenManager;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

import java.util.*;
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

    /** Function reference used to change screens (usually ScreenManager::navigateTo). */
    private Consumer<String> navigator;

    /** Optional ScreenManager reference kept for parity with the architecture. */
    private ScreenManager screenManager;

    // ----------- FXML-injected nodes -----------
    @FXML private StackPane root;
    @FXML private Button btnPlay, btnLeaderboard, btnChangeTeam, btnLogout;
    @FXML private ImageView bgImage;
    @FXML private Pane monsterLayer;
    @FXML private BorderPane uiRoot;

    // ----------- Roaming sprite state -----------
    private final List<Monster> monsters = new ArrayList<>();
    private AnimationTimer timer;
    private long lastNs = 0L;

    // ===== Fence play-area bounds (inset from layer edges so they bounce inside the fence tiles) =====
    // Tweak these 4 numbers to match your fence image thickness.
    private static final double INSET_LEFT   = 70;
    private static final double INSET_RIGHT  = 70;
    private static final double INSET_TOP    = 90;
    private static final double INSET_BOTTOM = 110;

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
        Platform.runLater(() -> btnPlay.requestFocus());
        root.sceneProperty().addListener((o, oldS, s) -> {
            if (s != null) {
                var url = getClass().getResource("/ui/mainmenu.css");
                if (url != null) {
                    String u = url.toExternalForm();
                    s.getStylesheets().remove(u);
                    s.getStylesheets().add(u);
                }
            }
        });


        root.setOnKeyPressed(e -> {
            if (Objects.requireNonNull(e.getCode()) == KeyCode.ESCAPE) onLogout();
        });

        loadBackground();
        spawnMonsters();
        startWander();

        root.sceneProperty().addListener((obs, oldS, newS) -> {
            if (newS != null) {
                bgImage.fitWidthProperty().bind(newS.widthProperty());
                bgImage.fitHeightProperty().bind(newS.heightProperty());
            }
        });

        bgImage.setViewOrder(10);
        monsterLayer.setViewOrder(5);
        uiRoot.setViewOrder(0);

        // Make sure the UI layer can receive input
        uiRoot.setMouseTransparent(false);

        // (Optional) if sprites were still covering, bring each button to front
        btnPlay.toFront();
        btnLeaderboard.toFront();
        btnChangeTeam.toFront();
        btnLogout.toFront();
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
     * Creates five monster sprites from {@code /images/Sprites/*.png}, sets size, full opacity,
     * random velocity, and attaches hover/click interactions.
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

            ImageView node = new ImageView(new Image(url.toExternalForm(), true));
            node.setPreserveRatio(true);
            node.setFitWidth(rand(70, 100)); // smaller per request
            node.setOpacity(1.0);            // not transparent
            node.setPickOnBounds(true);

            monsterLayer.getChildren().add(node);

            double vx = rand(35, 70) * (Math.random() < 0.5 ? -1 : 1);
            double vy = rand(25, 55) * (Math.random() < 0.5 ? -1 : 1);

            Monster m = new Monster(node, vx, vy);
            monsters.add(m);

            // Face initial direction
            node.setScaleX(vx < 0 ? -1 : 1);

            // Interactions: pause on hover, jump on click
            node.setOnMouseEntered(e -> m.setPaused(true));
            node.setOnMouseExited (e -> m.setPaused(false));
            node.setOnMouseClicked(e -> jump(node));
        }

        // Place once bounds are known
        monsterLayer.layoutBoundsProperty().addListener((o, ob, b) -> placeMonstersRandomly(b));
        if (monsterLayer.getLayoutBounds().getWidth() > 0) {
            placeMonstersRandomly(monsterLayer.getLayoutBounds());
        }
    }

    /**
     * Randomly places sprites within the fence play area.
     *
     * @param b the current layout bounds of the {@code monsterLayer}
     */
    private void placeMonstersRandomly(Bounds b) {
        double w = b.getWidth(), h = b.getHeight();
        if (w <= 0 || h <= 0) return;

        double left   = INSET_LEFT;
        double right  = w - INSET_RIGHT;
        double top    = INSET_TOP;
        double bottom = h - INSET_BOTTOM;

        for (Monster m : monsters) {
            ImageView node = m.node;
            double mw = node.getBoundsInParent().getWidth();
            double mh = node.getBoundsInParent().getHeight();
            double x = rand(left, Math.max(left, right - mw));
            double y = rand(top,  Math.max(top,  bottom - mh));
            node.setLayoutX(x);
            node.setLayoutY(y);
        }
    }

    /**
     * Starts the per-frame wander animation. Sprites bounce at fence edges, gently drift,
     * and add a small sinusoidal bob to feel alive. Hover pauses movement.
     */
    private void startWander() {
        timer = new AnimationTimer() {
            private double bobT = 0; // shared phase; fine for subtle coherence

            @Override public void handle(long now) {
                if (lastNs == 0L) { lastNs = now; return; }
                double dt = (now - lastNs) / 1_000_000_000.0;
                lastNs = now;

                bobT += dt;

                Bounds b = monsterLayer.getLayoutBounds();
                double w = b.getWidth(), h = b.getHeight();

                double left   = INSET_LEFT;
                double right  = w - INSET_RIGHT;
                double top    = INSET_TOP;
                double bottom = h - INSET_BOTTOM;

                for (Monster m : monsters) {
                    ImageView n = m.node;

                    if (m.paused) continue; // stop on hover

                    double x = n.getLayoutX() + m.vx * dt;
                    double y = n.getLayoutY() + m.vy * dt;

                    double mw = n.getBoundsInParent().getWidth();
                    double mh = n.getBoundsInParent().getHeight();

                    // Bounce on walls (reverse velocity)
                    if (x < left)            { x = left;             m.vx = Math.abs(m.vx); }
                    if (x > right - mw)      { x = right - mw;       m.vx = -Math.abs(m.vx); }
                    if (y < top)             { y = top;              m.vy = Math.abs(m.vy); }
                    if (y > bottom - mh)     { y = bottom - mh;      m.vy = -Math.abs(m.vy); }

                    // Flip facing by vx sign
                    n.setScaleX(m.vx < 0 ? -1 : 1);

                    // Organic drift: tiny random nudges + sinusoidal bob
                    m.vx = clamp(m.vx + rand(-4, 4) * dt * 25, -90, 90);
                    m.vy = clamp(m.vy + rand(-3, 3) * dt * 25, -70, 70);

                    double bob = Math.sin(bobT * (1.2 + m.bobSpeed)) * m.bobAmp; // 2–4 px bob
                    n.setLayoutX(x);
                    n.setLayoutY(y + bob);
                }
            }
        };
        timer.start();
    }

    /**
     * Plays a quick “hop” using a Timeline on the sprite node (no threads).
     *
     * @param node sprite ImageView
     */
    private void jump(ImageView node) {
        double startY = node.getLayoutY();
        double peakY  = startY - 26; // hop height

        Timeline tl = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(node.layoutYProperty(), startY, Interpolator.EASE_OUT),
                        new KeyValue(node.scaleXProperty(), node.getScaleX() * 1.0),
                        new KeyValue(node.scaleYProperty(), 1.0)
                ),
                new KeyFrame(Duration.millis(120),
                        new KeyValue(node.layoutYProperty(), peakY, Interpolator.EASE_OUT),
                        new KeyValue(node.scaleYProperty(), 1.07, Interpolator.EASE_OUT)
                ),
                new KeyFrame(Duration.millis(240),
                        new KeyValue(node.layoutYProperty(), startY, Interpolator.EASE_IN),
                        new KeyValue(node.scaleYProperty(), 1.0, Interpolator.EASE_IN)
                )
        );
        tl.play();
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

    /** Navigates to the Play screen. */
    @FXML private void onPlay() { navigate("PLAY"); }

    /** Navigates to the Leaderboard screen. */
    @FXML private void onLeaderboard() { navigate("LEADERBOARD"); }

    /** Navigates to the Player Profile screen. */
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

    // ===== simple record for sprite state =====
    private static class Monster {
        final ImageView node;
        double vx;
        double vy;
        boolean paused = false;
        // per-sprite bob parameters for variety
        final double bobAmp   = randStatic(2.0, 4.0);
        final double bobSpeed = randStatic(0.2, 0.8);

        Monster(ImageView node, double vx, double vy) {
            this.node = node; this.vx = vx; this.vy = vy;
        }
        void setPaused(boolean p) { this.paused = p; }
        private static double randStatic(double a, double b) {
            return ThreadLocalRandom.current().nextDouble(a, b);
        }
    }
}
