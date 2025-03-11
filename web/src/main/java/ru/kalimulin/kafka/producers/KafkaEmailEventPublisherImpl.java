package ru.kalimulin.kafka.producers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.kalimulin.dto.kafkaEventDTO.EmailNotificationEvent;
import ru.kalimulin.dto.kafkaEventDTO.WalletNotificationEvent;
import ru.kalimulin.kafka.KafkaEmailEventPublisher;

@Service
@RequiredArgsConstructor
public class KafkaEmailEventPublisherImpl implements KafkaEmailEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final Logger logger = LoggerFactory.getLogger(KafkaEmailEventPublisherImpl.class);

    @Override
    public void sendEmailNotification(EmailNotificationEvent event) {
        logger.info("Отправка email-уведомления в Kafka: {}", event);
        kafkaTemplate.send("email-notifications", event);
    }

    @Override
    public void sendWalletNotification(WalletNotificationEvent event) {
        logger.info("Отправка email-уведомления в Kafka");
        kafkaTemplate.send("wallet-email-notification", event);
    }
}