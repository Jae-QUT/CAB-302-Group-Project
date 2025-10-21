package com.therejects.cab302groupproject;

import com.therejects.cab302groupproject.Navigation.ScreenManager;

import com.therejects.cab302groupproject.model.AuthDatabase;
import javafx.application.Application;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
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
    public void start(Stage stage) throws IOException, SQLException {
        AuthDatabase.ensureSchema();
        ScreenManager sm = new ScreenManager(stage);
        sm.navigateTo("LOGOUT");
        stage.show();
        stage.setTitle("Login!");
    }

    public static void main(String[] args) {
        launch();
    }
}