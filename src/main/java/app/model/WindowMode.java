package app.model;

public enum WindowMode {
    // Определяем константы и передаем значения в конструктор
    FULLSCREEN(0, 0), // Размеры для полноэкранного режима обычно определяются динамически
    WINDOWED(800, 600),
    BORDERLESS(400, 200);

    private final int width;
    private final int height;

    // Конструктор для enum
    WindowMode(int width, int height) {
        this.width = width;
        this.height = height;
    }

    // Геттеры для доступа к полям
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
