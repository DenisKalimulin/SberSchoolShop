package ru.kalimulin.customExceptions.orderExceptions;

public class UnauthorizedOrderDeletionException extends RuntimeException {
    public UnauthorizedOrderDeletionException(String message) {
        super(message);
    }
}
