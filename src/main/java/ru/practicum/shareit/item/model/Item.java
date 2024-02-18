package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.user.User;

@Data
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private Item request;
}
