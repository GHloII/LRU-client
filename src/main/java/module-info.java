module myapp {
    requires javafx.controls;
    requires javafx.fxml;

    opens app.controller to javafx.fxml;
    opens app.view to javafx.fxml;
    exports app;
}
