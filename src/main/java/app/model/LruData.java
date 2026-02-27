package app.model;

public record LruData(int amount_of_pages_in_memory, int[] array_of_incoming_pages) {
}
