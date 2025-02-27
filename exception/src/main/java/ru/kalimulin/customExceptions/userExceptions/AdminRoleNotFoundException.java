package ru.kalimulin.customExceptions.userExceptions;

public class AdminRoleNotFoundException extends RuntimeException {
    public AdminRoleNotFoundException(String message) {
        super(message);
    }
}