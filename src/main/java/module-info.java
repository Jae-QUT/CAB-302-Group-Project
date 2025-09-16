module com.therejects.cab302groupproject {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires jdk.compiler;

    opens com.therejects.cab302groupproject to javafx.fxml;
    exports com.therejects.cab302groupproject;
    opens com.therejects.cab302groupproject.Navigation to javafx.fxml;
    exports com.therejects.cab302groupproject.Navigation;
    opens com.therejects.cab302groupproject.controller to javafx.fxml;
    exports com.therejects.cab302groupproject.controller;
    opens com.therejects.cab302groupproject.model to javafx.fxml;
    exports com.therejects.cab302groupproject.model;
}