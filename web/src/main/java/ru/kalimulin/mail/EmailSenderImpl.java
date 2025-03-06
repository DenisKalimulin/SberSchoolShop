package ru.kalimulin.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSenderImpl implements EmailSender {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;


    @Override
    public void sendOrderNotification(String sellerEmail, String productTitle, int quantity, String address) {
        String subject = "Ваш товар был куплен!";
        String body = String.format("Ваш товар \"%s\" был продан!" +
                        " Количество: %d. Проверьте ваш кошелек и отправьте товар в течении суток по адресу: %s",
                productTitle, quantity, address);
        sendEmail(sellerEmail, subject, body);
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }
}