package ru.kalimulin.kafka;

import ru.kalimulin.dto.kafkaEventDTO.EmailNotificationEvent;

public interface KafkaOrderEventPublisher {
    void sendEmailNotification(EmailNotificationEvent event);
}