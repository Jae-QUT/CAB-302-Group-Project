package com.therejects.cab302groupproject.controller;

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
    protected void onBackToMainMenu() throws IOException {
        Stage stage = (Stage) backToMenu.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(Leaderboard.class.getResource("MainMenu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), Leaderboard.WIDTH, Leaderboard.HEIGHT);
        stage.setScene(scene);
    }

    @FXML
    protected void onUsernameSearch() {

    }
}