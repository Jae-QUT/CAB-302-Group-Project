//package com.therejects.cab302groupproject;
//
//import com.therejects.cab302groupproject.controller.ProfileController;
//import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Scene;
//import javafx.stage.Stage;
//
//public class ProfileTestApp extends Application {
//    @Override
//    public void start(Stage stage) throws Exception {
//        FXMLLoader loader = new FXMLLoader(
//                getClass().getResource("/com/therejects/cab302groupproject/ProfileView.fxml")
//        );
//        Scene scene = new Scene(loader.load(), 500, 500);
//        stage.setTitle("Profile Test");
//        stage.setScene(scene);
//        stage.show();
//
//        // After scene loads, pass a username to simulate login
//        ProfileController controller = loader.getController();
//        controller.loadProfile("rob"); // replace with a username that exists in your DB
//    }
//
//    public static void main(String[] args) {
//        launch();
//    }
//}