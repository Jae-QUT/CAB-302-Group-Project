package com.therejects.cab302groupproject.controller;

import com.therejects.cab302groupproject.Navigation.*;
import com.therejects.cab302groupproject.Leaderboard;
import com.therejects.cab302groupproject.MainMenuLauncher;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;


public class LeaderboardController {
    @FXML
    private Pagination leaderboardPage;
    @FXML
    private Button backToMenu;
    @FXML
    private TextField usernameSearch;
    @FXML
    private GridPane leaderboardGrid;

    private ScreenManager screenManager;

    /**
     *
     * @return the screenManager if there is an issue injecting into the manager. It will return
     */
    public ScreenManager sm() {
        if (screenManager == null) {
            // fallback if someone forgot to inject; build from current window
            Stage stage = (Stage) backToMenu.getScene().getWindow();
            screenManager = new ScreenManager(stage);
        }
        return screenManager;
    }

    @FXML
    protected void onBackToMainMenu() throws IOException {
        sm().navigateTo("MAIN_MENU");
    }

    @FXML
    protected void onUsernameSearch() {

    }

    @FXML
    protected void leaderboardGrid() {

    }
}