package ru.kalimulin.kafka.consumers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.kalimulin.dto.kafkaEventDTO.WalletTransactionEvent;

@Service
public class WalletEventListener {
    private static final Logger logger = LoggerFactory.getLogger(WalletEventListener.class);

    @KafkaListener(topics = "wallet-transactions", groupId = "wallet-group")
    public void listen(WalletTransactionEvent event) {
        logger.info("Обработано событие: {} | Отправитель: {} | Получатель: {} | Сумма: {} | Время: {}",
                event.getTransactionType(),
                event.getSenderLogin(),
                event.getRecipientWallet(),
                event.getAmount(),
                event.getTimestamp());
    }
}