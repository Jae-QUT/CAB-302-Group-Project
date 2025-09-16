module com.therejects.cab302groupproject {
    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    // Extra UI libraries
    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;

    // com.therejects.cab302groupproject.model.Database + logging
    requires java.sql;
    requires java.logging;

    // Export base package
    exports com.therejects.cab302groupproject;
    opens com.therejects.cab302groupproject to javafx.fxml;

    // Export controller + model packages
    exports com.therejects.cab302groupproject.controller;
    opens com.therejects.cab302groupproject.controller to javafx.fxml;

    exports com.therejects.cab302groupproject.model;
    opens com.therejects.cab302groupproject.model to javafx.fxml;
}