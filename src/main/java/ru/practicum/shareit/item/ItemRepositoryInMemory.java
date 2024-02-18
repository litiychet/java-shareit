package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.IdFactory;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Qualifier("ItemRepositoryInMemory")
public class ItemRepositoryInMemory implements ItemRepository {
    private final List<Item> items = new ArrayList<>();
    private final IdFactory idFactory = new IdFactory();
    private final UserRepository userRepository;

    @Autowired
    public ItemRepositoryInMemory(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Item create(Long userId, Item item) {
        if (userRepository.getById(userId) == null)
            throw new NotFoundException("Пользователя с ID " + userId + " не найдено");

        item.setId(idFactory.getId());
        item.setOwner(userRepository.getById(userId));

        items.add(item);

        return item;
    }

    @Override
    public Item update(Long userId, Long itemId, Item item) {
        return items.stream()
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
                .findFirst()
                .orElse(null);
    }

    @Override
    public Item get(Long itemId) {
        return items.stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Item> getUserItems(Long userId) {
        return items.stream()
                .filter(i -> i.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchByName(String text) {
        if (text == null || text.isEmpty())
            return Collections.emptyList();
        return items.stream()
                .filter(i -> (i.getName().toLowerCase().contains(text.toLowerCase())
                        || i.getDescription().toLowerCase().contains(text.toLowerCase())
                        && i.getAvailable()))
                .collect(Collectors.toList());
    }
}