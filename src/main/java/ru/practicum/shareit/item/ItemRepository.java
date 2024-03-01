package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item create(Long userId, Item item);

    Optional<Item> update(Long userId, Long itemId, Item item);

    Optional<Item> get(Long itemId);

    List<Item> getUserItems(Long userId);

    List<Item> searchByName(String text);
}
