package ru.kalimulin.customExceptions.imageExceptions;

public class ImageLimitExceededException extends RuntimeException {
    public ImageLimitExceededException(String message) {
        super(message);
    }
}
