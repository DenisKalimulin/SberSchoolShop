package ru.kalimulin.customExceptions.userExceptions;

public class UserAlreadyHasAdminRoleException extends RuntimeException {
    public UserAlreadyHasAdminRoleException(String message) {
        super(message);
    }
}