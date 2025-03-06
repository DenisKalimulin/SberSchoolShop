package ru.kalimulin.kafka.consumers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.kalimulin.dto.kafkaEventDTO.EmailNotificationEvent;
import ru.kalimulin.mail.EmailSender;

@Service
@RequiredArgsConstructor
public class EmailEventListener {
    private final EmailSender emailSender;

    private static final Logger logger = LoggerFactory.getLogger(EmailEventListener.class);

    @KafkaListener(topics = "email-notifications", groupId = "email-group")
    public void listen(EmailNotificationEvent event) {
        logger.info("Получено email-уведомление из Kafka: {}", event);
        emailSender.sendOrderNotification(
                event.getSellerEmail(),
                event.getProductTitle(),
                event.getQuantity(),
                event.getAddress());

        logger.info("Email-уведомление отправлено продавцу: {}", event.getSellerEmail());
    }

}