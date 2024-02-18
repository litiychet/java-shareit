package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Autowired
    public ItemServiceImpl(@Qualifier("ItemRepositoryInMemory") ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        return ItemMapper.toItemDto(itemRepository.create(userId, ItemMapper.toItem(itemDto)));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        return ItemMapper.toItemDto(itemRepository.update(userId, itemId, ItemMapper.toItem(itemDto)));
    }

    @Override
    public ItemDto get(Long itemId) {
        return ItemMapper.toItemDto(itemRepository.get(itemId));
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
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
}
