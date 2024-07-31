package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Override
    public ItemRequestDto createRequest(long userId, ItemRequestCreateDto request) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователя с ID " + userId + " не найдено")
        );

        ItemRequest newRequest = ItemRequestMapper.itemRequestCreateDtoToItemRequest(request);

        newRequest.setRequestor(user);
        newRequest.setCreated(LocalDateTime.now());

        return ItemRequestMapper.itemRequestToItemRequestDto(
                itemRequestRepository.save(newRequest)
        );
    }

    @Override
    public List<ItemRequestWithItemsDto> getUserRequests(long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователя с ID " + userId + " не найдено")
        );

        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorId(userId);
        List<Long> requestsId = requests.stream()
                .map(r -> r.getId())
                .collect(toList());
        Map<Long, List<Item>> items = itemRepository.findAllByRequestIdIn(requestsId).stream()
                .collect(groupingBy(i -> i.getRequest().getId(), toList()));

        return requests.stream()
                .map(r -> ItemRequestWithItemsDto.builder()
                        .items(items.getOrDefault(r.getId(), List.of()).stream()
                                .map(ItemMapper::toRequestItemDto)
                                .collect(toList()))
                        .description(r.getDescription())
                        .created(r.getCreated())
                        .build())
                .sorted(Comparator.comparing(ItemRequestWithItemsDto::getCreated).reversed())
                .collect(toList());
    }

    @Override
    public List<ItemRequestDto> getRequests(long from, long size) {
        Pageable page = PageRequest.of((int) from, (int) size, Sort.by("created").descending());
        List<ItemRequest> requests = itemRequestRepository.findAll(page).toList();

        return requests.stream()
                .map(ItemRequestMapper::itemRequestToItemRequestDto)
                .collect(toList());
    }

    @Override
    public ItemRequestDto getRequest(long requestId) {
        Optional<ItemRequest> itemRequest = itemRequestRepository.findById(requestId);

        if (itemRequest.isEmpty())
            throw new NotFoundException("Запроса с ID " + requestId + " не найдено");

        List<ItemForRequestDto> items = itemRepository.findAllByRequestId(requestId).stream()
                .map(ItemMapper::toRequestItemDto)
                .collect(toList());

        ItemRequestDto itemRequestDto = ItemRequestMapper.itemRequestToItemRequestDto(itemRequest.get());
        itemRequestDto.setItems(items);
        return itemRequestDto;
    }
}
