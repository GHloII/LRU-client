package app.controller;

import app.model.LruData;
import app.service.TCPClient;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MainController {

    @FXML private TextField hostField;
    @FXML private TextField portField;
    @FXML private Button connectButton;
    @FXML private TextArea logArea;

    @FXML private TextField lruPagesInMemoryField;
    @FXML private TextField lruIncomingPagesField;
    @FXML private Button sendLruButton;
    @FXML private Button loadFromFileButton;
    @FXML private Button saveToFileButton;

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
    private void onSaveToFileButtonClick() {
        try {
            int pagesInMemory = Integer.parseInt(lruPagesInMemoryField.getText().trim());
            int[] incomingPages = Arrays.stream(lruIncomingPagesField.getText().split(","))
                                     .map(String::trim)
                                     .filter(s -> !s.isEmpty())
                                     .mapToInt(Integer::parseInt)
                                     .toArray();

            LruData data = new LruData(pagesInMemory, incomingPages);

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Сохранить настройки LRU");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON Files", "*.json")
            );
            
            fileChooser.setInitialFileName("lru_settings.json");
            
            File file = fileChooser.showSaveDialog(logArea.getScene().getWindow());
            
            if (file != null) {
                try (FileWriter writer = new FileWriter(file)) {
                    gson.toJson(data, writer);
                    logArea.appendText("Настройки успешно сохранены в файл: " + file.getName() + "\n");
                }
            }
        } catch (NumberFormatException e) {
            logArea.appendText("Ошибка: Проверьте правильность введенных данных. " +
                             "Количество страниц должно быть числом, а последовательность - числами через запятую.\n");
        } catch (IOException e) {
            logArea.appendText("Ошибка при сохранении файла: " + e.getMessage() + "\n");
        } catch (Exception e) {
            logArea.appendText("Неизвестная ошибка при сохранении: " + e.getMessage() + "\n");
        }
    }

    @FXML
    private void onLoadFromFileButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите JSON файл с данными LRU");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );
        File selectedFile = fileChooser.showOpenDialog(logArea.getScene().getWindow());

        if (selectedFile != null) {
            try (FileReader reader = new FileReader(selectedFile)) {
                LruData data = gson.fromJson(reader, LruData.class);

                if (data == null || data.array_of_incoming_pages() == null) {
                     logArea.appendText("Ошибка: Неверный формат JSON. Ожидались поля 'amount_of_pages_in_memory' и 'array_of_incoming_pages'.\n");
                     return;
                }

                lruPagesInMemoryField.setText(String.valueOf(data.amount_of_pages_in_memory()));
                String pages = Arrays.stream(data.array_of_incoming_pages())
                                     .mapToObj(String::valueOf)
                                     .collect(Collectors.joining(", "));
                lruIncomingPagesField.setText(pages);

                logArea.appendText("Данные успешно загружены из файла: " + selectedFile.getName() + "\n");

            } catch (IOException e) {
                logArea.appendText("Ошибка чтения файла: " + e.getMessage() + "\n");
            } catch (JsonSyntaxException e) {
                logArea.appendText("Ошибка парсинга JSON: Файл не является корректным JSON. " + e.getMessage() + "\n");
            } catch (Exception e) {
                logArea.appendText("Произошла непредвиденная ошибка: " + e.getMessage() + "\n");
            }
        }
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
        loadFromFileButton.setDisable(disabled);
        saveToFileButton.setDisable(disabled);
        lruPagesInMemoryField.setDisable(disabled);
        lruIncomingPagesField.setDisable(disabled);
    }

    private String parseAndFormatLruResponse(String message) {
        if (message == null || !message.startsWith("Сервер: Result{")) {
            return message;
        }

        try {
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
            return message;
        }
    }
}

