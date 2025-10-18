module com.therejects.cab302groupproject {
    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    // Extra UI libraries
    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
//    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires jdk.compiler;

    //  com.example.mon.app.Database; + logging
    requires java.sql;
    requires java.logging;
    requires jakarta.mail;

    opens com.therejects.cab302groupproject to javafx.fxml;
    exports com.therejects.cab302groupproject;
    opens com.therejects.cab302groupproject.Navigation to javafx.fxml;
    exports com.therejects.cab302groupproject.Navigation;
    opens com.therejects.cab302groupproject.controller to javafx.fxml;
    exports com.therejects.cab302groupproject.controller;
    opens com.therejects.cab302groupproject.model to javafx.fxml;
    exports com.therejects.cab302groupproject.model;
    opens com.example.mon.app to javafx.fxml;
    exports com.example.mon.app;
}