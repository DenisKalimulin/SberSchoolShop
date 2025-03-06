package ru.kalimulin.customExceptions.addressExceptions;

public class UserHasNoAddressException extends RuntimeException {
    public UserHasNoAddressException(String message) {
        super(message);
    }
}