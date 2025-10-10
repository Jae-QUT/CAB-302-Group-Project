package com.therejects.cab302groupproject;

import com.therejects.cab302groupproject.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;

import java.io.IOException;

public class Leaderboard extends Application {

    public static final String TITLE = "Leaderboard";
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 700;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Leaderboard.class.getResource("/com/therejects/cab302groupproject/Leaderboard-view.fxml"));
//        Parent root = FXMLLoader.load(Leaderboard.class.getResource("Leaderboard-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Leaderboard");
//        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }
}
