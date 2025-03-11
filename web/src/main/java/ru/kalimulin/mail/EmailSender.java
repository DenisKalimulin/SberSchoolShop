package ru.kalimulin.mail;

import ru.kalimulin.models.Address;

public interface EmailSender {
    void sendOrderNotification(String sellerEmail, String productTitle, int quantity, String address);

    void sendWalletNotification(String email, String subject, String message);
}
