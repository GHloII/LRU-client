package app.controller;

import app.service.MessageService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {

    private final MessageService messageService = new MessageService();

    @FXML
    private Label label;

    @FXML
    private void onButtonClick() {
        label.setText(messageService.getClickMessage());
    }

    @FXML
    private void initialize() {
        label.setText(messageService.getInitialMessage());
    }
}
