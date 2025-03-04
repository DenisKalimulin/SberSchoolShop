package ru.kalimulin.customExceptions.orderExceptions;

public class OrderCannotBeDeletedException extends RuntimeException {
    public OrderCannotBeDeletedException(String message) {
        super(message);
    }

}
