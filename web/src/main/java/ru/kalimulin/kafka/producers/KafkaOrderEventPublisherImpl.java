package ru.kalimulin.kafka.producers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.kalimulin.dto.kafkaEventDTO.EmailNotificationEvent;
import ru.kalimulin.kafka.KafkaOrderEventPublisher;

@Service
@RequiredArgsConstructor
public class KafkaOrderEventPublisherImpl implements KafkaOrderEventPublisher {
    private final KafkaTemplate<String, EmailNotificationEvent> kafkaTemplate;

    private static final Logger logger = LoggerFactory.getLogger(KafkaOrderEventPublisherImpl.class);

    @Override
    public void sendEmailNotification(EmailNotificationEvent event) {
        logger.info("Отправка email-уведомления в Kafka: {}", event);
        kafkaTemplate.send("email-notifications", event);
    }
}