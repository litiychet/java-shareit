package ru.practicum.shareit.exception;

public class AlreadyApprovedException extends RuntimeException {
    public AlreadyApprovedException(String msg) {
        super(msg);
    }
}
