package com.therejects.cab302groupproject;
import com.therejects.cab302groupproject.Navigation.ScreenManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
//import java.util.function.Consumer;


public class MainMenuLauncher extends Application {
//    private Consumer<String> navigator;

//    public void setNavigator(Consumer<String> navigator) {
//        this.navigator = navigator;
//    }

    @Override
    public void start(Stage stage) throws IOException {
        ScreenManager sm = new ScreenManager(stage);
        sm.navigateTo("LOGOUT");
        stage.show();

//        FXMLLoader fxmlLoader = new FXMLLoader(MainMenuLauncher.class.getResource("/com/therejects/cab302groupproject/MainMenu.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
//        Object controller = fxmlLoader.getController();
//        System.out.println("Loaded controller class: " + (controller != null ? controller.getClass() : "null"));

        stage.setTitle("Hello!");
//        stage.setScene(scene);
//        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}