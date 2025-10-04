package com.therejects.cab302groupproject.model;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BattleApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Load the Monster Selection screen first
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/therejects/cab302groupproject/MonsterSelection.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 980, 560); // you can adjust size as needed
        stage.setScene(scene);
        stage.setTitle("Select Your Monsters");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
