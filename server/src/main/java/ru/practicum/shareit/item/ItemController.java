package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HeaderName;
import ru.practicum.shareit.ValidateMarker;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemCreateDto create(@RequestHeader(HeaderName.USER_ID) final long userId,
                                @Validated(ValidateMarker.Create.class) @RequestBody ItemCreateDto item) {
        log.info("POST /items {} {}: {}", item, HeaderName.USER_ID, userId);
        return itemService.create(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemCreateDto update(@RequestHeader(HeaderName.USER_ID) final long userId,
                                @PathVariable Long itemId,
                                @Validated(ValidateMarker.Update.class) @RequestBody ItemCreateDto item) {
        log.info("PATCH /items/{} {} {}: {}", itemId, item, HeaderName.USER_ID, userId);
        return itemService.update(userId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto get(@RequestHeader(HeaderName.USER_ID) final long userId,
                               @PathVariable Long itemId) {
        log.info("GET /items/{} {}: {}", itemId, HeaderName.USER_ID, userId);
        return itemService.get(itemId, userId);
    }

    @GetMapping
    public List<ItemResponseDto> getUserItems(@RequestHeader(HeaderName.USER_ID) final long userId) {
        log.info("GET /items {}: {}", HeaderName.USER_ID, userId);
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public List<ItemCreateDto> searchByName(@RequestParam String text) {
        if (text.isEmpty())
            return Collections.emptyList();
        log.info("GET /items/search?text={}", text);
        return itemService.searchItem(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto createCommentToItem(@RequestHeader(HeaderName.USER_ID) final long userId,
                                                  @PathVariable Long itemId,
                                                  @Validated(ValidateMarker.Create.class) @RequestBody CommentCreateDto comment) {
        log.info("POST /items/{}/comment {} {}: {}", itemId, comment, HeaderName.USER_ID, userId);
        return itemService.addComment(userId, itemId, comment);
    }
}
