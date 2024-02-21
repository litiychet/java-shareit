package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(@Qualifier("ItemRepositoryInMemory") ItemRepository itemRepository,
                           @Qualifier("UserRepositoryInMemory") UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        validateExistsUser(userId);
        Item createItem = ItemMapper.toItem(itemDto);
        User owner = userRepository.getById(userId).get();
        createItem.setOwner(userRepository.getById(userId).get());
        return ItemMapper.toItemDto(itemRepository.create(userId, createItem));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        validateExistsItem(itemId);
        validateExistsUser(userId);
        return ItemMapper.toItemDto(
                itemRepository.update(userId, itemId, ItemMapper.toItem(itemDto)).get()
        );
    }

    @Override
    public ItemDto get(Long itemId) {
        validateExistsItem(itemId);
        return ItemMapper.toItemDto(
                itemRepository.get(itemId).get()
        );
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        validateExistsUser(userId);
        return itemRepository.getUserItems(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        return itemRepository.searchByName(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void validateExistsItem(Long itemId) {
        if (itemRepository.get(itemId).isEmpty())
            throw new NotFoundException("Вещи с ID " + itemId + " не найдено");
    }

    private void validateExistsUser(Long userId) {
        if (userRepository.getById(userId).isEmpty())
            throw new NotFoundException("Пользователя с ID " + userId + " не найдено");
    }
}
