module com.therejects.cab302groupproject {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.therejects.cab302groupproject to javafx.fxml;
    exports com.therejects.cab302groupproject;
}
