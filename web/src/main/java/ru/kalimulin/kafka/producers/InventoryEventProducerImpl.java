package ru.kalimulin.kafka.producers;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.kalimulin.dto.kafkaEventDTO.InventoryUpdateEvent;
import ru.kalimulin.kafka.InventoryEventProducer;

@Service
@RequiredArgsConstructor
public class InventoryEventProducerImpl implements InventoryEventProducer {
    private final KafkaTemplate<String, InventoryUpdateEvent> kafkaTemplate;

    @Override
    public void sendInventoryUpdate(Long productId, int quantitySold) {
        InventoryUpdateEvent event = new InventoryUpdateEvent(productId, quantitySold);
        kafkaTemplate.send("inventory-updates", event);
    }
}