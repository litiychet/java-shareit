package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto create(Long userId, BookingCreateDto bookingCreateDto) {
        validateExistsUser(userId);
        validateExistsItem(bookingCreateDto.getItemId());

        if (bookingCreateDto.getEnd().isBefore(LocalDateTime.now())
                || bookingCreateDto.getStart().isBefore(LocalDateTime.now())
                || bookingCreateDto.getEnd().isBefore(bookingCreateDto.getStart())
                || bookingCreateDto.getEnd().isEqual(bookingCreateDto.getStart()))
            throw new DateBookingException("Некорректные даты бронированя");

        Item item = itemRepository.findById(bookingCreateDto.getItemId()).get();
        if (!item.getAvailable())
            throw new ItemUnvailableException("Предмет с ID " + item.getId() + " не доступен");

        if (item.getOwner().getId().equals(userId))
            throw new NotFoundException("Пользователь с ID "
                    + userId
                    + " является владельцем вещи с ID "
                    + item.getId());

        Booking newBooking = BookingMapper.fromBookingCreateDto(bookingCreateDto);
        newBooking.setBooker(userRepository.findById(userId).get());
        newBooking.setItem(itemRepository.findById(bookingCreateDto.getItemId()).get());
        newBooking.setStatus(BookingStatus.WAITING);

        return BookingMapper.toBookingDto(
                bookingRepository.save(newBooking)
        );
    }

    @Override
    public BookingDto changeStatus(Long userId, Long bookingId, Boolean approved) {
        validateExistsUser(userId);

        Booking dbBooking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирования с ID " + bookingId + " не найдено"));

        if (!dbBooking.getItem().getOwner().getId().equals(userId))
            throw new NotFoundException(
                    "Пользователь c ID " + userId + " не является владельцем вещи " + dbBooking.getItem().getId()
            );

        if (dbBooking.getStatus().equals(BookingStatus.APPROVED))
            throw new AlreadyApprovedException("Бронирование с ID " + bookingId + " уже подтвержденно");

        if (approved) {
            dbBooking.setStatus(BookingStatus.APPROVED);
        } else {
            dbBooking.setStatus(BookingStatus.REJECTED);
        }

        return BookingMapper.toBookingDto(bookingRepository.save(dbBooking));
    }

    @Override
    public BookingDto get(Long userId, Long bookingId) {
        Booking dbBooking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирования с ID " + bookingId + " не найдено"));

        if (dbBooking.getBooker().getId().equals(userId)
                || dbBooking.getItem().getOwner().getId().equals(userId))
            return BookingMapper.toBookingDto(dbBooking);

        throw new NotFoundException(
                "Пользователь с ID " + userId + " не является владельцем бронирования или вещи"
        );
    }

    @Override
    public List<BookingDto> getBookings(Long userId, BookingState bookingState) {
        validateExistsUser(userId);

        switch (bookingState) {
            case ALL:
                return bookingRepository.findAllByBookerId(userId).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findCurrentBooking(userId).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findPastBooking(userId).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findFutureBooking(userId).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findBookingByStatus(userId, BookingStatus.WAITING).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findBookingByStatus(userId, BookingStatus.REJECTED).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                return Collections.emptyList();
        }
    }

    @Override
    public List<BookingDto> getBookingItemOwner(Long ownerId, BookingState bookingState) {
        validateExistsUser(ownerId);

        if (itemRepository.countByOwnerId(ownerId) == 0)
            throw new NotFoundException("У пользователя " + ownerId + " нет вещей");

        switch (bookingState) {
            case ALL:
                return bookingRepository.findBookingByItemsOwner(ownerId).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findCurrentBookingByItemsOwner(ownerId).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findPastBookingByItemsOwner(ownerId).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findFutureBookingByItemsOwner(ownerId).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findBookingByOwnerIdAndStatus(ownerId, BookingStatus.WAITING).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findBookingByOwnerIdAndStatus(ownerId, BookingStatus.REJECTED).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                return Collections.emptyList();
        }
    }

    private void validateExistsUser(Long userId) {
        if (userRepository.findById(userId).isEmpty())
            throw new NotFoundException("Пользователя с ID " + userId + " не найдено");
    }

    private void validateExistsItem(Long itemId) {
        if (itemRepository.findById(itemId).isEmpty())
            throw new NotFoundException("Вещи с ID " + itemId + " не найдено");
    }
}