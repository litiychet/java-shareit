package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.IdFactory;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Qualifier("ItemRepositoryInMemory")
public class ItemRepositoryInMemory implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private final IdFactory idFactory = new IdFactory();

    @Override
    public Item create(Long userId, Item item) {
        item.setId(idFactory.getId());

        items.put(item.getId(), item);

        return item;
    }

    @Override
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

    @Override
    public Optional<Item> get(Long itemId) {
        return items.values().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst();
    }

    @Override
    public List<Item> getUserItems(Long userId) {
        return items.values().stream()
                .filter(i -> i.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchByName(String text) {
        return items.values().stream()
                .filter(i -> (i.getName().toLowerCase().contains(text.toLowerCase())
                        || i.getDescription().toLowerCase().contains(text.toLowerCase())
                        && i.getAvailable()))
                .collect(Collectors.toList());
    }
}