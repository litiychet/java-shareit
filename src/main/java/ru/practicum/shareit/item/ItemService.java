package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {
    ItemCreateDto create(Long userId, ItemCreateDto itemCreateDto);

    ItemCreateDto update(Long userId, Long itemId, ItemCreateDto itemCreateDto);

    ItemResponseDto get(Long itemId, Long userId);

    List<ItemResponseDto> getUserItems(Long userId);

    List<ItemCreateDto> searchItem(String text);

    CommentResponseDto addComment(Long userId, Long itemId, CommentCreateDto commentCreateDto);
}
