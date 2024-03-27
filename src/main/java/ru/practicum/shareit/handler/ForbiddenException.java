package ru.practicum.shareit.handler;

public class ForbiddenException extends RuntimeException{
    public ForbiddenException(String message) {
        super(message);
    }

}
