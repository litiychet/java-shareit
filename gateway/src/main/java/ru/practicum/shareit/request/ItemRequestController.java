package ru.practicum.shareit.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.HeaderName;
import ru.practicum.shareit.ValidateMarker;

@Slf4j
@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(HeaderName.USER_ID) final long userId,
                                        @Validated(ValidateMarker.Create.class) @RequestBody ItemRequestCreateDto request) {
        log.info("POST /requests {} {}: {}", request, HeaderName.USER_ID, userId);
        return itemRequestClient.createRequest(userId, request);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader(HeaderName.USER_ID) final long userId) {
        log.info("GET /requests {}: {}", HeaderName.USER_ID, userId);
        return itemRequestClient.getUserRequests(userId);
    }

    @Validated
    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestParam @PositiveOrZero long from,
                                               @RequestParam @Positive long size) {
        log.info("GET /requests/all?from={}&size={}", from, size);
        return itemRequestClient.getRequests(from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@PathVariable long requestId) {
        log.info("GET /requests/{}", requestId);
        return itemRequestClient.getRequest(requestId);
    }
}
