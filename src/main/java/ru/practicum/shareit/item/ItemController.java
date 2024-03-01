package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.ValidateMarker;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") final long userId,
                       @Validated(ValidateMarker.Create.class) @RequestBody ItemDto item) {
        log.info("POST /items");
        log.info("X-Sharer-User-Id: {}", userId);
        log.info("Create item {} by user {}", item, userId);
        return itemService.create(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") final long userId,
                       @PathVariable Long itemId,
                       @Validated(ValidateMarker.Update.class) @RequestBody ItemDto item) {
        log.info("PATCH /items/{}", itemId);
        log.info("X-Sharer-User-Id: {}", userId);
        log.info("Update item {} by user {}", itemId, userId);
        log.info("New item {}", item);
        return itemService.update(userId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable Long itemId) {
        log.info("GET /items/{}", itemId);
        return itemService.get(itemId);
    }

    @GetMapping
    public List<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") final long userId) {
        log.info("GET /items");
        log.info("X-Sharer-User-Id: {}", userId);
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchByName(@RequestParam String text) {
        if (text.isEmpty())
            return Collections.emptyList();
        log.info("GET /items/search?text={}", text);
        return itemService.searchItem(text);
    }
}
