package ru.kalimulin.kafka;

import ru.kalimulin.dto.kafkaEventDTO.EmailNotificationEvent;
import ru.kalimulin.dto.kafkaEventDTO.WalletNotificationEvent;

public interface KafkaEmailEventPublisher {
    void sendEmailNotification(EmailNotificationEvent event);
    void sendWalletNotification(WalletNotificationEvent event);
}