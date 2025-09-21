package com.therejects.cab302groupproject;

import com.therejects.cab302groupproject.Navigation.ScreenManager;

import javafx.application.Application;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.function.Consumer;

/**
 *
 */
public class MainMenuLauncher extends Application {
    /**
     * The injection point to the app
     * @param stage The current Screen that is being displayed
     * @throws IOException Exception for the screens
     */
    @Override
    public void start(Stage stage) throws IOException {
        ScreenManager sm = new ScreenManager(stage);
        sm.navigateTo("LOGOUT");
        stage.show();
        stage.setTitle("Login!");
    }

    public static void main(String[] args) {
        launch();
    }
}