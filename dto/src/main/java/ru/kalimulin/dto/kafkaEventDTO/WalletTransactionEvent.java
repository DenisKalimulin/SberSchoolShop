package ru.kalimulin.dto.kafkaEventDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletTransactionEvent {
    private String transactionType;
    private String senderLogin;
    private String recipientWallet;
    private BigDecimal amount;
    private Instant timestamp;
}