package ru.kalimulin.enums;

public enum OrderStatus {
    PENDING,   // Заказ создан, но не оплачен
    PAID,      // Заказ оплачен
    SHIPPED,   // Заказ отправлен
    DELIVERED, // Заказ доставлен
    CANCELLED  // Заказ отменен
}