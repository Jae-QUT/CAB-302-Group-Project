package com.therejects.cab302groupproject.Navigation;

import com.therejects.cab302groupproject.controller.MainMenuController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ScreenManager {
    private final Stage stage;

    public ScreenManager(Stage stage) {
        this.stage = stage;
    }

    public void showMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/therejects/cab302groupproject/MainMenu.fxml")
            );
            Parent root = loader.load();

            MainMenuController controller = loader.getController();
            controller.setNavigator(this::navigateTo);

            Scene scene = new Scene(root, 1024, 640);
            scene.getStylesheets().add(
                    getClass().getResource("/theme/theme.css").toExternalForm()
            );
            stage.setScene(scene);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load Main Menu", e);
        }
    }

    public void navigateTo(String screenId) {
        switch (screenId) {
            case "PLAY"        -> System.out.println("TODO: go to Play screen");
            case "LEADERBOARD" -> System.out.println("TODO: go to Leaderboard screen");
            case "CHANGE_TEAM" -> System.out.println("TODO: go to Change Team screen");
            case "LOGOUT"      -> System.out.println("TODO: back to login screen");
            default            -> System.err.println("Unknown screen: " + screenId);
        }
    }
}
