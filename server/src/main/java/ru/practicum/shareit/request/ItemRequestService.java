package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.List;

public interface ItemRequestService {
    public ItemRequestDto createRequest(long userId, ItemRequestCreateDto request);

    public List<ItemRequestWithItemsDto> getUserRequests(long userId);

    public List<ItemRequestDto> getRequests(long from, long size);

    public ItemRequestDto getRequest(long requestId);
}