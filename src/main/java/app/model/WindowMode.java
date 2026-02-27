package app.model;

public enum WindowMode {
    FULLSCREEN(0, 0),
    WINDOWED(800, 600),
    BORDERLESS(400, 200);

    private final int width;
    private final int height;

    WindowMode(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
