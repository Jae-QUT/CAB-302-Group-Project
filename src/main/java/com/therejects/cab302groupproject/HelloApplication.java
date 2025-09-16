package com.therejects.cab302groupproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxml = new FXMLLoader(HelloApplication.class.getResource("/com/therejects/cab302groupproject/login-view.fxml"));
        Scene scene = new Scene(fxml.load(), 980, 560);
        stage.setTitle("<Math Monsters> — Sign in");

        // <-- Access to Xavier's Profile -->
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/therejects/cab302groupproject/ProfileView.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
//        stage.setTitle("Profile Test!");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}