package com.therejects.cab302groupproject.Navigation;

import com.example.mon.app.*;
import com.therejects.cab302groupproject.controller.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Management system to easily add new screens and switch between them when needed.
 */
public class ScreenManager {

    /**
     * The values that get added when we incorporate a new screen and the constructor
     */
    public enum Screen
    {
        MAIN_MENU("/com/therejects/cab302groupproject/MainMenu.fxml", "Main Menu"),
        PLAY("/com/therejects/cab302groupproject/battle-view.fxml", "Battle View"),
        LEADERBOARD("/com/therejects/cab302groupproject/Leaderboard.fxml", "Leaderboard"),
        PLAYER_PROFILE("/com/therejects/cab302groupproject/ProfileView.fxml", "Player Profile"),
        LOGOUT("/com/therejects/cab302groupproject/login-view.fxml", "Login");
//        REGISTER_DIALOG("/com/example/mon/app/RegisterDialog.java", "Register Dialog");

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

            var cssUrl = getClass().getResource("/theme/theme.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("Warning: /theme/theme.css not found on classpath");
            }

            stage.setTitle(screen.title());
            stage.setScene(scene);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load " + screen, e);
        }
    }

//    < NEED TO COME BACK AND CHANGE THESE AS THEY ARE PLACEHOLDERS >

    /**
     * Function that is used to determine which {@link Screen} is assigned to each displau
     * @param screenId is the enumerable that was assigned in {@link Screen}
     */
    public void navigateTo(String screenId) {
        switch (screenId) {
            case "PLAY"        -> loadScreen(Screen.PLAY);
            case "LEADERBOARD" -> loadScreen(Screen.LEADERBOARD);
            case "PLAYER_PROFILE" -> loadScreen(Screen.PLAYER_PROFILE);
            case "LOGOUT"      -> loadScreen(Screen.LOGOUT);
            case "MAIN_MENU" -> loadScreen(Screen.MAIN_MENU);       //
//            case "REGISTER_DIALOG" -> loadScreen(Screen.REGISTER_DIALOG);
//            case "" -> loadScreen(Screen.);
//            case "" -> loadScreen(Screen.);
            default            -> System.err.println("Unknown screen: " + screenId);
        }
    }
}
