package com.therejects.cab302groupproject.controller;

import com.therejects.cab302groupproject.Navigation.ScreenManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;

import java.util.Objects;
import java.util.function.Consumer;

public class MainMenuController {
    private Consumer<String> navigator;

    @FXML private StackPane root;
    @FXML private Button btnPlay, btnLeaderboard, btnChangeTeam, btnLogout;

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

    @FXML private void onLogout()
    { navigate("LOGOUT");

    }

    private void navigate(String screenId) {
        if (navigator != null) navigator.accept(screenId);
        else System.err.println("Navigator not set for: " + screenId);
    }

}
