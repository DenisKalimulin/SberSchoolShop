package ru.kalimulin.kafka.consumers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.kalimulin.customExceptions.productExceptions.NotEnoughStockException;
import ru.kalimulin.customExceptions.productExceptions.ProductNotFoundException;
import ru.kalimulin.dto.kafkaEventDTO.InventoryUpdateEvent;
import ru.kalimulin.models.Product;
import ru.kalimulin.repositories.ProductRepository;

@Service
@RequiredArgsConstructor
public class InventoryEventListener {
    private final ProductRepository productRepository;
    private static final Logger logger = LoggerFactory.getLogger(InventoryEventListener.class);

    @KafkaListener(topics = "inventory-updates", groupId = "inventory-group")
    public void listen(InventoryUpdateEvent event) {
        Product product = productRepository.findById(event.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Товар не найден"));

        if (product.getStocks() < event.getQuantitySold()) {
            throw new NotEnoughStockException("Недостаточно товара на складе");
        }

        product.setStocks(product.getStocks() - event.getQuantitySold());
        productRepository.save(product);

        logger.info("Обновлен остаток товара с id {}", product.getId());
    }
}
