package app.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class TCPClient extends Service<Void> {

    private final String host;
    private final int port;
    private PrintWriter out;
    private Socket socket;
    private Consumer<String> onMessageReceived;

    public TCPClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void setOnMessageReceived(Consumer<String> onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    socket = new Socket(host, port);
                    out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    if (onMessageReceived != null) {
                        onMessageReceived.accept("Успешно подключено к " + host + ":" + port);
                    }

                    String fromServer;
                    while ((fromServer = in.readLine()) != null) {
                        if (onMessageReceived != null) {
                            onMessageReceived.accept("Сервер: " + fromServer);
                        }
                    }
                } catch (IOException e) {
                    if (onMessageReceived != null) {
                        onMessageReceived.accept("Ошибка: " + e.getMessage());
                    }
                } finally {
                    if (socket != null) {
                        socket.close();
                    }
                    if (onMessageReceived != null) {
                        onMessageReceived.accept("Соединение закрыто.");
                    }
                }
                return null;
            }
        };
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    public void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
        }
        cancel();
    }
}
