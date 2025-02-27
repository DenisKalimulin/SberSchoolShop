package ru.kalimulin.customExceptions.reviewExceptions;

public class ReviewException extends RuntimeException {
    public ReviewException(String message) {
        super(message);
    }
}
