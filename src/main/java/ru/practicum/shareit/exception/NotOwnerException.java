package ru.practicum.shareit.exception;

public class NotOwnerException extends RuntimeException {
    public NotOwnerException(String msg) {
        super(msg);
    }
}
