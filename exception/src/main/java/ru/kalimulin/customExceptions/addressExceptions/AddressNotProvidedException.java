package ru.kalimulin.customExceptions.addressExceptions;

public class AddressNotProvidedException extends RuntimeException {
    public AddressNotProvidedException(String message) {
        super(message);
    }
}