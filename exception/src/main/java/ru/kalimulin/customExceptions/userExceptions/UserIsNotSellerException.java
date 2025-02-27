package ru.kalimulin.customExceptions.userExceptions;

public class UserIsNotSellerException extends RuntimeException{
    public UserIsNotSellerException (String message) {
        super(message);
    }
}
