package ru.kalimulin.kafka;

public interface InventoryEventProducer {
    void sendInventoryUpdate(Long productId, int quantitySold);
}
