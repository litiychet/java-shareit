package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.HeaderName;
import ru.practicum.shareit.ValidateMarker;

import java.util.Collections;

@Slf4j
@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HeaderName.USER_ID) final long userId,
                                 @Validated(ValidateMarker.Create.class) @RequestBody ItemCreateDto item) {
        log.info("POST /items {} {}: {}", item, HeaderName.USER_ID, userId);
        return itemClient.create(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(HeaderName.USER_ID) final long userId,
                                @PathVariable Long itemId,
                                @Validated(ValidateMarker.Update.class) @RequestBody ItemCreateDto item) {
        log.info("PATCH /items/{} {} {}: {}", itemId, item, HeaderName.USER_ID, userId);
        return itemClient.update(userId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@RequestHeader(HeaderName.USER_ID) final long userId,
                               @PathVariable Long itemId) {
        log.info("GET /items/{} {}: {}", itemId, HeaderName.USER_ID, userId);
        return itemClient.get(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader(HeaderName.USER_ID) final long userId) {
        log.info("GET /items {}: {}", HeaderName.USER_ID, userId);
        return itemClient.getUserItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchByName(@RequestParam String text) {
        if (text.isBlank())
            return ResponseEntity.ok(Collections.emptyList());
        log.info("GET /items/search?text={}", text);
        return itemClient.searchItem(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createCommentToItem(@RequestHeader(HeaderName.USER_ID) final long userId,
                                                  @PathVariable Long itemId,
                                                  @Validated(ValidateMarker.Create.class) @RequestBody CommentCreateDto comment) {
        log.info("POST /items/{}/comment {} {}: {}", itemId, comment, HeaderName.USER_ID, userId);
        return itemClient.addComment(userId, itemId, comment);
    }
}
