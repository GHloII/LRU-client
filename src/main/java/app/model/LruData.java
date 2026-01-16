package app.model;

/**
 * DTO (Data Transfer Object) для отправки данных на LRU-сервер.
 */
public record LruData(int amount_of_pages_in_memory, int[] array_of_incoming_pages) {
}
