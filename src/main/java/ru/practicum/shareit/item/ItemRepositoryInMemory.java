package ru.practicum.shareit.item;

import ru.practicum.shareit.IdFactory;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

public class ItemRepositoryInMemory {
    private final Map<Long, Item> items = new HashMap<>();
    private final IdFactory idFactory = new IdFactory();

    public Item create(Long userId, Item item) {
        item.setId(idFactory.getId());

        items.put(item.getId(), item);

        return item;
    }

    public Optional<Item> update(Long userId, Long itemId, Item item) {
        return items.values().stream()
                .filter(i -> i.getId().equals(itemId))
                .peek(i -> {
                    if (!i.getOwner().getId().equals(userId))
                        throw new NotOwnerException(
                                "Пользователь c ID " + userId + " не является владельцем вещи " + itemId
                        );
                    if (item.getName() != null)
                        i.setName(item.getName());
                    if (item.getDescription() != null)
                        i.setDescription(item.getDescription());
                    if (item.getAvailable() != null)
                        i.setAvailable(item.getAvailable());
                })
                .findFirst();
    }

    public Optional<Item> get(Long itemId) {
        return items.values().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst();
    }

    public List<Item> getUserItems(Long userId) {
        return items.values().stream()
                .filter(i -> i.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Item> searchByName(String text) {
        return items.values().stream()
                .filter(i -> (i.getName().toLowerCase().contains(text.toLowerCase())
                        || i.getDescription().toLowerCase().contains(text.toLowerCase())
                        && i.getAvailable()))
                .collect(Collectors.toList());
    }
}