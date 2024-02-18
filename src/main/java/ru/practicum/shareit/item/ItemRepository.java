package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item create(Long userId, Item item);

    Item update(Long userId, Long itemId, Item item);

    Item get(Long itemId);

    List<Item> getUserItems(Long userId);

    List<Item> searchByName(String text);
}
