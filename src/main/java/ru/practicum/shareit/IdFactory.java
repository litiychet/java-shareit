package ru.practicum.shareit;

public class IdFactory {
    private Long id;

    public IdFactory() {
        this.id = 0L;
    }

    public Long getId() {
        return ++id;
    }
}
