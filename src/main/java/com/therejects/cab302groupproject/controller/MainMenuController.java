package com.therejects.cab302groupproject.controller;

//import com.example.mon.app.LoginScreenFX;
import com.therejects.cab302groupproject.model.User;
import com.therejects.cab302groupproject.Navigation.ScreenManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * The main menu controller uses the loadScreen method from the ScreenManager to direct the player/user based on
 * which button they press. This method allows us to easily switch between stages.
 */
public class MainMenuController {
    private Consumer<String> navigator;
    private ScreenManager screenManager;


    @FXML private StackPane root;
    @FXML private Button btnPlay, btnLeaderboard, btnChangeTeam, btnLogout;

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

    @FXML
    private void initialize() {
        Platform.runLater(() -> btnPlay.requestFocus());
        root.setOnKeyPressed(e -> {
            if (Objects.requireNonNull(e.getCode()) == KeyCode.ESCAPE) {
                onLogout(); // ESC to logout
            }
        });
    }


    @FXML private void onPlay() {
        navigate("PLAY");
    }

    @FXML private void onLeaderboard() {
        navigate("LEADERBOARD");
    }

    @FXML private void onPlayerProfile()
    { navigate("PLAYER_PROFILE");
    }

    @FXML private void onBackToMainMenu(){
        navigate("LEADERBOARD");
    }

    @FXML private void onLogout()
    {try {
        User.setCurrentUser(null);
        javafx.stage.Stage stage = (javafx.stage.Stage) root.getScene().getWindow();
        LoginScreenFX login = new LoginScreenFX();
        login.start(stage); // reuses the same stage
//    { navigate("LOGOUT");
    } catch (Exception e) {
        e.printStackTrace();
    }
    }

    private void navigate(String screenId) {
        if (navigator != null) navigator.accept(screenId);
        else System.err.println("Navigator not set for: " + screenId);
    }

}
