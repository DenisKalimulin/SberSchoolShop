package ru.kalimulin.dto.kafkaEventDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryUpdateEvent {
    private Long productId;
    private int quantitySold;
}
