package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotBookerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemCreateDto create(Long userId, ItemCreateDto itemCreateDto) {
        validateExistsUser(userId);
        Item createItem = ItemMapper.toItem(itemCreateDto);
        User owner = userRepository.findById(userId).get();
        createItem.setOwner(owner);
        return ItemMapper.toItemDto(itemRepository.save(createItem));
    }

    @Override
    public ItemCreateDto update(Long userId, Long itemId, ItemCreateDto itemCreateDto) {
        validateExistsItem(itemId);
        validateExistsUser(userId);

        Item dbItem = itemRepository.findById(itemId).get();

        if (!dbItem.getOwner().getId().equals(userId))
            throw new NotOwnerException(
                "Пользователь c ID " + userId + " не является владельцем вещи " + itemId
            );

        if (itemCreateDto.getName() != null)
            dbItem.setName(itemCreateDto.getName());
        if (itemCreateDto.getDescription() != null)
            dbItem.setDescription(itemCreateDto.getDescription());
        if (itemCreateDto.getAvailable() != null)
            dbItem.setAvailable(itemCreateDto.getAvailable());

        return ItemMapper.toItemDto(
                itemRepository.save(dbItem)
        );
    }

    @Override
    public ItemResponseDto get(Long itemId, Long userId) {
        validateExistsItem(itemId);
        validateExistsUser(userId);

        Item item = itemRepository.findById(itemId).get();

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
        validateExistsUser(userId);
        return itemRepository.findAllByOwnerId(userId).stream()
                .map(ItemMapper::toItemResponseDto)
                .peek(i -> {
                    i.setLastBooking(getLastBookingForItem(i.getId()));
                    i.setNextBooking(getNextBookingForItem(i.getId()));
                    i.setComments(commentRepository.findAllByItemId(i.getId()).stream()
                            .map(CommentMapper::toCommentResponseDto)
                            .collect(Collectors.toList()));
                })
                .sorted((i1, i2) -> (int) (i1.getId() - i2.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemCreateDto> searchItem(String text) {
        return itemRepository.findByNameOrDescriptionLike(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponseDto addComment(Long userId, Long itemId, CommentCreateDto commentCreateDto) {
        validateExistsUser(userId);
        validateExistsItem(itemId);

        if (bookingRepository.findPastBooking(userId).isEmpty()
                || bookingRepository.findCurrentBooking(userId).isEmpty())
            throw new NotBookerException("Пользователь " + userId + " не брал вещь " + itemId + " в аренду");

        Comment createdComment = CommentMapper.toComment(commentCreateDto);
        createdComment.setItem(itemRepository.findById(itemId).get());
        createdComment.setAuthor(userRepository.findById(userId).get());
        createdComment.setCreated(LocalDateTime.now());

        return CommentMapper.toCommentResponseDto(
                commentRepository.save(createdComment)
        );
    }

    private void validateExistsItem(Long itemId) {
        if (itemRepository.findById(itemId).isEmpty())
            throw new NotFoundException("Вещи с ID " + itemId + " не найдено");
    }

    private void validateExistsUser(Long userId) {
        if (userRepository.findById(userId).isEmpty())
            throw new NotFoundException("Пользователя с ID " + userId + " не найдено");
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
