package ru.kalimulin.customExceptions.favoriteExceptions;

public class FavoriteNotFoundException extends RuntimeException {
    public FavoriteNotFoundException(String message) {
        super(message);
    }
}
