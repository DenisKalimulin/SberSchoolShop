package ru.kalimulin.kafka;

import ru.kalimulin.dto.kafkaEventDTO.WalletTransactionEvent;

public interface WalletEventProducer {
    void sendWalletTransaction(WalletTransactionEvent event);
}