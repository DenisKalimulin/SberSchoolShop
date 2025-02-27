package ru.kalimulin.customExceptions.walletExceptions;

public class InvalidPinException extends RuntimeException {
    public InvalidPinException(String message) {
        super(message);
    }
}
