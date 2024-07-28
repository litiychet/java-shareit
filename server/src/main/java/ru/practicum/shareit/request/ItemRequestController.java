package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.HeaderName;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.ValidateMarker;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader(HeaderName.USER_ID) final long userId,
                                        @Validated(ValidateMarker.Create.class) @RequestBody ItemRequestCreateDto request) {
        log.info("POST /requests {} {}: {}", request, HeaderName.USER_ID, userId);
        return itemRequestService.createRequest(userId, request);
    }

    @GetMapping
    public List<ItemRequestWithItemsDto> getUserRequests(@RequestHeader(HeaderName.USER_ID) final long userId) {
        log.info("GET /requests {}: {}", HeaderName.USER_ID, userId);
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestParam(defaultValue = "0") long from,
                                               @RequestParam(defaultValue = "3") long size) {
        log.info("GET /requests/all?from={}&size={}", from, size);
        return itemRequestService.getRequests(from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@PathVariable long requestId) {
        log.info("GET /requests/{}", requestId);
        return itemRequestService.getRequest(requestId);
    }
}
