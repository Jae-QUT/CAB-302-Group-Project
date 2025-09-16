package com.therejects.cab302groupproject.Navigation;

import com.sun.tools.javac.Main;
import com.therejects.cab302groupproject.controller.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ScreenManager {

    public enum Screen
    {
        MAIN_MENU("/com/therejects/cab302groupproject/MainMenu.fxml", "Main Menu"),
        PLAY("/com/therejects/cab302groupproject/Play.fxml", "Play"),
        LEADERBOARD("/com/therejects/cab302groupproject/Leaderboard.fxml", "Leaderboard"),
        CHANGE_TEAM("/com/therejects/cab302groupproject/ChangeTeam.fxml", "Change Team"),
        LOGIN("/com/therejects/cab302groupproject/Login.fxml", "Login");

        private final String fxml;
        private final String title;
        Screen(String fxml, String title) {
            this.fxml = fxml;
            this.title = title;
        }
        // Constructors for assigning which fxml file the paths go to and their title
        public String fxml() { return fxml; }
        public String title() { return title; }

    }

    private final Stage stage;

    public ScreenManager(Stage stage) {
        this.stage = stage;
    }


    private void loadScreen(Screen screen) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(screen.fxml()));
            Parent root = loader.load();

            // Optional: if the controller implements an interface, inject navigator
            if (loader.getController() instanceof MainMenuController c) {
                c.setNavigator(this::navigateTo);
            }

            Scene scene = new Scene(root, 1024, 640);
            scene.getStylesheets().add(
                    getClass().getResource("/theme/theme.css").toExternalForm()
            );
            stage.setTitle(screen.title());
            stage.setScene(scene);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load " + screen, e);
        }
    }

//          < Redundant. We have the "loadScreen" function now which is a new flow for this >
//    public void showMainMenu() {
//        try {
//            FXMLLoader loader = new FXMLLoader(
//                    getClass().getResource("/com/therejects/cab302groupproject/MainMenu.fxml")
//            );
//            Parent root = loader.load();
//
//            MainMenuController controller = loader.getController();
//            controller.setNavigator(this::navigateTo);
//
//            Scene scene = new Scene(root, 1024, 640);
//            scene.getStylesheets().add(
//                    getClass().getResource("/theme/theme.css").toExternalForm()
//            );
//            stage.setScene(scene);
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to load Main Menu", e);
//        }
//    }

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
