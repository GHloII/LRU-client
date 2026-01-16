package app.controller;

import app.model.LruData;
import app.service.TCPClient;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainController {

    @FXML private TextField hostField;
    @FXML private TextField portField;
    @FXML private Button connectButton;
    @FXML private TextArea logArea;

    @FXML private TextField lruPagesInMemoryField;
    @FXML private TextField lruIncomingPagesField;
    @FXML private Button sendLruButton;

    private Gson gson = new Gson();

    private TCPClient tcpClient;

    @FXML
    private void initialize() {
        setControlsDisabled(true);
    }

    @FXML
    private void onConnectButtonClick() {
        if (tcpClient != null && tcpClient.isRunning()) {
            tcpClient.closeConnection();
            return;
        }

        String host = hostField.getText();
        int port;
        try {
            port = Integer.parseInt(portField.getText());
        } catch (NumberFormatException e) {
            logArea.appendText("Ошибка: Неверный формат порта.\n");
            return;
        }

        tcpClient = new TCPClient(host, port);

        tcpClient.setOnMessageReceived(message -> {
            Platform.runLater(() -> {
                String formattedMessage = parseAndFormatLruResponse(message);
                logArea.appendText(formattedMessage + "\n");
            });
        });

        tcpClient.setOnSucceeded(event -> resetUI());
        tcpClient.setOnFailed(event -> resetUI());
        tcpClient.setOnCancelled(event -> resetUI());

        tcpClient.start();

        connectButton.setText("Отключиться");
        setControlsDisabled(false);
        hostField.setDisable(true);
        portField.setDisable(true);
    }


    @FXML
    private void onSendLruButtonClick() {
        if (tcpClient == null || !tcpClient.isRunning()) {
            logArea.appendText("Ошибка: вы не подключены к серверу.\n");
            return;
        }

        try {
            int pagesInMemory = Integer.parseInt(lruPagesInMemoryField.getText());
            int[] incomingPages = Arrays.stream(lruIncomingPagesField.getText().split(","))
                                        .map(String::trim)
                                        .mapToInt(Integer::parseInt)
                                        .toArray();

            LruData lruData = new LruData(pagesInMemory, incomingPages);
            String jsonPayload = gson.toJson(lruData);

            logArea.appendText("Вы (LRU): " + jsonPayload + "\n");
            tcpClient.sendMessage(jsonPayload);

        } catch (NumberFormatException e) {
            logArea.appendText("Ошибка: неверный формат чисел в полях LRU.\n");
        }
    }

    private void resetUI() {
        connectButton.setText("Подключиться");
        setControlsDisabled(true);
        hostField.setDisable(false);
        portField.setDisable(false);
    }

    private void setControlsDisabled(boolean disabled) {
        sendLruButton.setDisable(disabled);
        lruPagesInMemoryField.setDisable(disabled);
        lruIncomingPagesField.setDisable(disabled);
    }

    private String parseAndFormatLruResponse(String message) {
        if (message == null || !message.startsWith("Сервер: Result{")) {
            return message;
        }

        try {
            // Определяем паттерны для извлечения значений
            Pattern interruptionsPattern = Pattern.compile("interuptions=(\\d+)");
            Pattern pagesPattern = Pattern.compile("amount_of_pages_in_memory=(\\d+)");
            Pattern arrayPattern = Pattern.compile("resultArray=(\\[.*\\])");

            Matcher interruptionsMatcher = interruptionsPattern.matcher(message);
            Matcher pagesMatcher = pagesPattern.matcher(message);
            Matcher arrayMatcher = arrayPattern.matcher(message);

            String interruptions = interruptionsMatcher.find() ? interruptionsMatcher.group(1) : "N/A";
            String pagesInMemory = pagesMatcher.find() ? pagesMatcher.group(1) : "N/A";
            String resultArray = arrayMatcher.find() ? arrayMatcher.group(1) : "N/A";

            return String.format("Ответ от LRU сервера:\n\t- Прерывания: %s\n\t- Страниц в памяти: %s\n\t- Итоговый массив: %s",
                                 interruptions, pagesInMemory, resultArray);

        } catch (Exception e) {
            logArea.appendText("Ошибка парсинга ответа от сервера: " + e.getMessage() + "\n");
            return message; // Возвращаем исходное сообщение в случае ошибки
        }
    }
}

