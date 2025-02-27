package ru.kalimulin.customExceptions.orderExceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException (String message){
        super(message);
    }
}
