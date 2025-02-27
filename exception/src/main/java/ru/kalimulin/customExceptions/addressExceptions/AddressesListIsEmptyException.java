package ru.kalimulin.customExceptions.addressExceptions;

public class AddressesListIsEmptyException extends RuntimeException {
    public AddressesListIsEmptyException(String message) {
        super(message);
    }
}
