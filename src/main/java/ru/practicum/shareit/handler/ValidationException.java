package ru.practicum.shareit.handler;

public class ValidationException extends RuntimeException{
    public ValidationException(String message){
        super(message);
    }
}
