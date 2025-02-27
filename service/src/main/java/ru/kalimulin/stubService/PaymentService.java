package ru.kalimulin.stubService;

import ru.kalimulin.models.User;

import java.math.BigDecimal;

public interface PaymentService {

    boolean processPayment(String userLogin, BigDecimal amount);

    boolean processPayment(User user, BigDecimal amount);

    void withdrawFunds(String userLogin, BigDecimal amount);
}
