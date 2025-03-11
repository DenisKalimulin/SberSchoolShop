package ru.kalimulin.kafka.producers;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.kalimulin.dto.kafkaEventDTO.WalletTransactionEvent;
import ru.kalimulin.kafka.WalletEventProducer;

@Service
@RequiredArgsConstructor
public class WalletEventProducerImpl implements WalletEventProducer {
    private final KafkaTemplate<String, WalletTransactionEvent> kafkaTemplate;

    @Override
    public void sendWalletTransaction(WalletTransactionEvent event) {
        kafkaTemplate.send("wallet-transactions", event);
    }
}