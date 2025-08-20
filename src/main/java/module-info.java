module com.therejects.cab302groupproject {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires com.almasb.fxgl.all;

    opens com.therejects.cab302groupproject to javafx.fxml;
    exports com.therejects.cab302groupproject;
}