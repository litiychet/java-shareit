package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotBookerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemCreateDto create(Long userId, ItemCreateDto itemCreateDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователя с ID " + userId + " не найдено")
        );


        Item createItem = ItemMapper.toItem(itemCreateDto);
        createItem.setOwner(user);
        if (itemCreateDto.getRequestId() != null) {
            Optional<ItemRequest> itemRequest = itemRequestRepository.findById(itemCreateDto.getRequestId());
            log.info("request find: {}", itemRequest);
            if (!itemRequest.isEmpty())
                createItem.setRequest(itemRequest.get());
        }

        return ItemMapper.toItemDto(itemRepository.save(createItem));
    }

    @Override
    public ItemCreateDto update(Long userId, Long itemId, ItemCreateDto itemCreateDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователя с ID " + userId + " не найдено")
        );

        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Вещи с ID " + itemId + " не найдено")
        );

        if (!item.getOwner().getId().equals(userId))
            throw new NotOwnerException(
                "Пользователь c ID " + userId + " не является владельцем вещи " + itemId
            );

        if (itemCreateDto.getName() != null && !itemCreateDto.getName().isBlank())
            item.setName(itemCreateDto.getName());
        if (itemCreateDto.getDescription() != null && !itemCreateDto.getDescription().isBlank())
            item.setDescription(itemCreateDto.getDescription());
        if (itemCreateDto.getAvailable() != null)
            item.setAvailable(itemCreateDto.getAvailable());

        return ItemMapper.toItemDto(
                itemRepository.save(item)
        );
    }

    @Override
    public ItemResponseDto get(Long itemId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователя с ID " + userId + " не найдено")
        );

        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Вещи с ID " + itemId + " не найдено")
        );

        ItemResponseDto itemResponseDto = ItemMapper.toItemResponseDto(item);

        itemResponseDto.setComments(commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::toCommentResponseDto)
                .collect(Collectors.toList()));

        if (!item.getOwner().getId().equals(userId))
            return itemResponseDto;

        itemResponseDto.setLastBooking(getLastBookingForItem(itemId));
        itemResponseDto.setNextBooking(getNextBookingForItem(itemId));

        return itemResponseDto;
    }

    @Override
    public List<ItemResponseDto> getUserItems(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователя с ID " + userId + " не найдено")
        );

        List<Item> userItems = itemRepository.findAllByOwnerId(userId);
        List<Long> itemsId = userItems.stream().map(Item::getId).toList();

        Map<ItemResponseDto, List<Comment>> itemComments = commentRepository.findByItemsIdIn(itemsId).stream()
                .collect(groupingBy(i -> ItemMapper.toItemResponseDto(i.getItem()), toList()));

        Map<ItemResponseDto, List<Booking>> lastBookingForItems = bookingRepository.findLastBookingByItemIn(itemsId).stream()
                .collect(groupingBy(i -> ItemMapper.toItemResponseDto(i.getItem()), toList()));

        Map<ItemResponseDto, List<Booking>> nextBookingForItems = bookingRepository.findNextBookingByItemIn(itemsId).stream()
                .collect(groupingBy(i -> ItemMapper.toItemResponseDto(i.getItem()), toList()));

        return userItems.stream()
                .map(ItemMapper::toItemResponseDto)
                .peek(i -> {
                    List<Comment> comments = itemComments.getOrDefault(i, List.of());
                    List<Booking> lastBooking = lastBookingForItems.get(i);
                    List<Booking> nextBooking = nextBookingForItems.get(i);

                    i.setComments(comments.stream()
                            .map(CommentMapper::toCommentResponseDto)
                            .collect(toList())
                    );

                    if (lastBooking != null) {
                        i.setLastBooking(BookingMapper.toBookingForItemDto(lastBooking.getFirst()));
                    } else {
                        i.setLastBooking(null);
                    }

                    if (nextBooking != null) {
                        i.setNextBooking(BookingMapper.toBookingForItemDto(nextBooking.getFirst()));
                    } else {
                        i.setNextBooking(null);
                    }
                })
                .collect(toList());
    }

    @Override
    public List<ItemCreateDto> searchItem(String text) {
        return itemRepository.findByNameOrDescriptionLike(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponseDto addComment(Long userId, Long itemId, CommentCreateDto commentCreateDto) {
        User author = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователя с ID " + userId + " не найдено")
        );

        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Вещи с ID " + itemId + " не найдено")
        );

        if (!bookingRepository.existsCurrentAndPastBookingByUserId(userId))
            throw new NotBookerException("Пользователь " + userId + " не брал вещь " + itemId + " в аренду");

        Comment createdComment = CommentMapper.toComment(commentCreateDto);
        createdComment.setItem(item);
        createdComment.setAuthor(author);
        createdComment.setCreated(LocalDateTime.now());

        return CommentMapper.toCommentResponseDto(
                commentRepository.save(createdComment)
        );
    }

    private BookingForItemDto getLastBookingForItem(Long itemId) {
        List<Booking> bookings = bookingRepository.findLastBookingByItemId(itemId);
        Optional<Booking> optBooking = bookings.stream().findFirst();
        if (optBooking.isEmpty())
            return null;
        Booking booking = optBooking.get();
        return BookingForItemDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    private BookingForItemDto getNextBookingForItem(Long itemId) {
        List<Booking> bookings = bookingRepository.findNextBookingByItemId(itemId);
        Optional<Booking> optBooking = bookings.stream().findFirst();
        if (optBooking.isEmpty())
            return null;
        Booking booking = optBooking.get();
        return BookingForItemDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
