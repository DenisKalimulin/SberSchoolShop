package ru.kalimulin.dto.kafkaEventDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletNotificationEvent {
    private String email;
    private String subject;
    private String message;
}
