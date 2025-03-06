package ru.kalimulin.dto.kafkaEventDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailNotificationEvent implements Serializable {
    private String sellerEmail;
    private String productTitle;
    private int quantity;
    private String address;
}
