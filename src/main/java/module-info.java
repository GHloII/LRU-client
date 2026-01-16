module myapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens app.controller to javafx.fxml;
    opens app.model to com.google.gson;
    exports app;
}
