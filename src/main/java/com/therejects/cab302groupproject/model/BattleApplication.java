package com.therejects.cab302groupproject.model;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BattleApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxml = new FXMLLoader(BattleApplication.class.getResource("/com/therejects/cab302groupproject/battle-view.fxml"));
        Scene scene = new Scene(fxml.load(), 600, 400);
        stage.setScene(scene);
        stage.setTitle("Pokémon Battle GUI");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
