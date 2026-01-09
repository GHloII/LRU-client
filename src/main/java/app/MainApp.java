package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import app.model.StartWindowData;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/app/view/ui.fxml")
        );
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(MainApp.class.getResource("/app/view/styles.css").toExternalForm());

        stage.setTitle(StartWindowData.TITEL_STRING);
        stage.setScene(scene);
        stage.setWidth(StartWindowData.WINDOW_MODE.getWidth());
        stage.setHeight(StartWindowData.WINDOW_MODE.getHeight());
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
