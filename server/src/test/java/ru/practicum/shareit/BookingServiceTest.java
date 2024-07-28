package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.AlreadyApprovedException;
import ru.practicum.shareit.exception.DateBookingException;
import ru.practicum.shareit.exception.ItemUnvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Booking pastBooking;
    private Booking currentBooking;
    private Booking futureBooking;
    private Booking waitingBooking;

    private User user1;
    private User user2;
    private User user3;

    private Item item1;
    private Item item2;

    private LocalDateTime pastStartTime;
    private LocalDateTime pastEndTime;
    private LocalDateTime currentStartTime;
    private LocalDateTime currentEndTime;
    private LocalDateTime futureStartTime;
    private LocalDateTime futureEndTime;

    @BeforeEach
    public void setup() {
        bookingRepository = Mockito.mock(BookingRepository.class);
        itemRepository = Mockito.mock(ItemRepository.class);
        userRepository = Mockito.mock(UserRepository.class);

        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);

        pastStartTime = LocalDateTime.now()
                .minus(1, ChronoUnit.MONTHS)
                .plus(1, ChronoUnit.DAYS);

        pastEndTime = LocalDateTime.now()
                .minus(7, ChronoUnit.DAYS);

        currentStartTime = LocalDateTime.now()
                .minus(5, ChronoUnit.DAYS);

        currentEndTime = LocalDateTime.now()
                .plus(5, ChronoUnit.DAYS);

        futureStartTime = LocalDateTime.now()
                .plus(7, ChronoUnit.DAYS);

        futureEndTime = LocalDateTime.now()
                .plus(1, ChronoUnit.MONTHS)
                .plus(5, ChronoUnit.DAYS);

        user1 = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("user2")
                .email("user2@mail.ru")
                .build();

        user3 = User.builder()
                .id(3L)
                .name("user3")
                .email("user3@mail.ru")
                .build();

        item1 = Item.builder()
                .id(1L)
                .name("item 1")
                .description("item 1 description")
                .available(true)
                .owner(user1)
                .build();

        item2 = Item.builder()
                .id(2L)
                .name("item 2")
                .description("item 2 description")
                .available(false)
                .owner(user2)
                .build();

        pastBooking = Booking.builder()
                .id(1L)
                .start(pastStartTime)
                .end(pastEndTime)
                .booker(user2)
                .item(item1)
                .status(BookingStatus.APPROVED)
                .build();

        currentBooking = Booking.builder()
                .id(2L)
                .start(currentStartTime)
                .end(currentEndTime)
                .booker(user1)
                .item(item2)
                .status(BookingStatus.APPROVED)
                .build();

        futureBooking = Booking.builder()
                .id(3L)
                .start(futureStartTime)
                .end(futureEndTime)
                .booker(user3)
                .item(item1)
                .status(BookingStatus.APPROVED)
                .build();

        waitingBooking = Booking.builder()
                .id(4L)
                .start(futureStartTime)
                .end(futureEndTime)
                .booker(user3)
                .item(item1)
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    public void createBookingCorrect() {
        futureBooking.setStatus(BookingStatus.WAITING);

        BookingCreateDto createBookingDto = BookingCreateDto.builder()
                .start(futureBooking.getStart())
                .end(futureBooking.getEnd())
                .itemId(futureBooking.getItem().getId())
                .build();

        given(itemRepository.findById(item1.getId())).willReturn(Optional.of(item1));
        given(userRepository.findById(user3.getId())).willReturn(Optional.of(user3));
        given(bookingRepository.save(BookingMapper.fromBookingCreateDto(createBookingDto)))
                .willReturn(futureBooking);

        BookingDto responseDto = bookingService.create(user3.getId(), createBookingDto);

        assertAll(
                "Verify create Booking",
                () -> assertEquals(3L, responseDto.getId()),
                () -> assertEquals(futureStartTime, responseDto.getStart()),
                () -> assertEquals(futureEndTime, responseDto.getEnd()),
                () -> assertEquals(1L, responseDto.getItem().getId()),
                () -> assertEquals(BookingStatus.WAITING.name(), responseDto.getStatus())
        );
    }

    @Test
    public void createBookingWithNotExistsUser() {
        BookingCreateDto createBookingDto = BookingCreateDto.builder()
                .start(futureBooking.getStart())
                .end(futureBooking.getEnd())
                .itemId(futureBooking.getItem().getId())
                .build();

        given(userRepository.findById(4L)).willReturn(Optional.empty());

        assertThrowsExactly(NotOwnerException.class, () -> bookingService.create(4L, createBookingDto));
    }

    @Test
    public void createBookingWithNotExistsItem() {
        BookingCreateDto createBookingDto = BookingCreateDto.builder()
                .start(futureBooking.getStart())
                .end(futureBooking.getEnd())
                .itemId(4L)
                .build();


        given(userRepository.findById(user3.getId())).willReturn(Optional.of(user3));
        given(itemRepository.findById(4L)).willReturn(Optional.empty());

        assertThrowsExactly(NotFoundException.class, () -> bookingService.create(user3.getId(), createBookingDto));
    }

    @Test
    public void createBookingWithNotCorrectDate() {
        BookingCreateDto createBookingDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plus(1, ChronoUnit.DAYS))
                .end(LocalDateTime.now().minus(1, ChronoUnit.DAYS))
                .itemId(item1.getId())
                .build();

        given(userRepository.findById(user3.getId())).willReturn(Optional.of(user3));
        given(itemRepository.findById(item1.getId())).willReturn(Optional.of(item1));

        assertThrowsExactly(DateBookingException.class, () -> bookingService.create(user3.getId(), createBookingDto));

        createBookingDto.setStart(LocalDateTime.now().plus(2, ChronoUnit.DAYS));
        createBookingDto.setEnd(LocalDateTime.now().plus(1, ChronoUnit.DAYS));

        assertThrowsExactly(DateBookingException.class, () -> bookingService.create(user3.getId(), createBookingDto));

        createBookingDto.setStart(LocalDateTime.now().plus(1, ChronoUnit.DAYS));
        createBookingDto.setEnd(createBookingDto.getStart());

        assertThrowsExactly(DateBookingException.class, () -> bookingService.create(user3.getId(), createBookingDto));
    }

    @Test
    public void createBookingWithUnvailableItem() {
        BookingCreateDto createBookingDto = BookingCreateDto.builder()
                .start(futureBooking.getStart())
                .end(futureBooking.getEnd())
                .itemId(item1.getId())
                .build();

        item1.setAvailable(false);

        given(userRepository.findById(user3.getId())).willReturn(Optional.of(user3));
        given(itemRepository.findById(item1.getId())).willReturn(Optional.of(item1));

        assertThrowsExactly(ItemUnvailableException.class, () -> bookingService.create(user3.getId(), createBookingDto));
    }

    @Test
    public void createBookingWithOwnerItem() {
        BookingCreateDto createBookingDto = BookingCreateDto.builder()
                .start(futureBooking.getStart())
                .end(futureBooking.getEnd())
                .itemId(item1.getId())
                .build();

        given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));
        given(itemRepository.findById(item1.getId())).willReturn(Optional.of(item1));

        assertThrowsExactly(NotFoundException.class, () -> bookingService.create(user1.getId(), createBookingDto));
    }

    @Test
    public void changeBookingStatusToApprove() {
        given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));
        given(bookingRepository.findById(waitingBooking.getId()))
                .willReturn(Optional.of(waitingBooking));
        given(bookingRepository.save(any())).willReturn(waitingBooking);

        BookingDto responseDto = bookingService.changeStatus(user1.getId(), waitingBooking.getId(), true);

        assertAll(
                "Verify change Booking status",
                () -> assertEquals(4L, responseDto.getId()),
                () -> assertEquals(futureStartTime, responseDto.getStart()),
                () -> assertEquals(futureEndTime, responseDto.getEnd()),
                () -> assertEquals(1L, responseDto.getItem().getId()),
                () -> assertEquals(BookingStatus.APPROVED.name(), responseDto.getStatus())
        );
    }

    @Test
    public void changeBookingStatusToReject() {
        given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));
        given(bookingRepository.findById(waitingBooking.getId()))
                .willReturn(Optional.of(waitingBooking));
        given(bookingRepository.save(any())).willReturn(waitingBooking);

        BookingDto responseDto = bookingService.changeStatus(user1.getId(), waitingBooking.getId(), false);

        assertAll(
                "Verify change Booking status",
                () -> assertEquals(4L, responseDto.getId()),
                () -> assertEquals(futureStartTime, responseDto.getStart()),
                () -> assertEquals(futureEndTime, responseDto.getEnd()),
                () -> assertEquals(1L, responseDto.getItem().getId()),
                () -> assertEquals(BookingStatus.REJECTED.name(), responseDto.getStatus())
        );
    }

    @Test
    public void changeNotExistsBookingStatus() {
        assertThrowsExactly(NotOwnerException.class, () -> bookingService.changeStatus(
                user1.getId(),
                waitingBooking.getId(),
                true));
    }

    @Test
    public void changeBookingStatusByNotItemOwner() {
        given(userRepository.findById(user3.getId())).willReturn(Optional.of(user3));
        given(bookingRepository.findById(waitingBooking.getId()))
                .willReturn(Optional.of(waitingBooking));

        assertThrowsExactly(NotOwnerException.class, () -> bookingService.changeStatus(
                user3.getId(),
                waitingBooking.getId(),
                true));
    }

    @Test
    public void changeBookingStatusToAlreadyApproved() {
        given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));
        waitingBooking.setStatus(BookingStatus.APPROVED);
        given(bookingRepository.findById(waitingBooking.getId()))
                .willReturn(Optional.of(waitingBooking));

        assertThrowsExactly(AlreadyApprovedException.class, () -> bookingService.changeStatus(
                user1.getId(),
                waitingBooking.getId(),
                true));
    }

    @Test
    public void getBookingByItemOwner() {
        given(bookingRepository.findById(futureBooking.getId()))
                .willReturn(Optional.of(futureBooking));

        BookingDto responseDto = bookingService.get(user1.getId(), futureBooking.getId());

        assertAll(
                "Verify get Booking",
                () -> assertEquals(3L, responseDto.getId()),
                () -> assertEquals(futureStartTime, responseDto.getStart()),
                () -> assertEquals(futureEndTime, responseDto.getEnd()),
                () -> assertEquals(1L, responseDto.getItem().getId()),
                () -> assertEquals(BookingStatus.APPROVED.name(), responseDto.getStatus())
        );
    }

    @Test
    public void getBookingByBooker() {
        given(bookingRepository.findById(futureBooking.getId()))
                .willReturn(Optional.of(futureBooking));

        BookingDto responseDto = bookingService.get(user3.getId(), futureBooking.getId());

        assertAll(
                "Verify get Booking",
                () -> assertEquals(3L, responseDto.getId()),
                () -> assertEquals(futureStartTime, responseDto.getStart()),
                () -> assertEquals(futureEndTime, responseDto.getEnd()),
                () -> assertEquals(1L, responseDto.getItem().getId()),
                () -> assertEquals(BookingStatus.APPROVED.name(), responseDto.getStatus())
        );
    }

    @Test
    public void getBookingByNotItemOwnerOrBooker() {
        given(bookingRepository.findById(futureBooking.getId()))
                .willReturn(Optional.of(futureBooking));

        assertThrowsExactly(NotFoundException.class, () -> bookingService.get(
                user2.getId(),
                futureBooking.getId())
        );
    }

    @Test
    public void getAllBookingsByItemOwner() {
        given(bookingRepository.findBookingByItemsOwner(user1.getId()))
                .willReturn(List.of(futureBooking, pastBooking));
        given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));
        given(itemRepository.countByOwnerId(user1.getId())).willReturn(2L);

        List<BookingDto> bookings = bookingService.getBookingItemOwner(user1.getId(), BookingState.ALL);

        assertAll(
                "Verify get Booking",
                () -> assertEquals(3L, bookings.get(0).getId()),
                () -> assertEquals(futureStartTime, bookings.get(0).getStart()),
                () -> assertEquals(futureEndTime, bookings.get(0).getEnd()),
                () -> assertEquals(1L, bookings.get(0).getItem().getId()),
                () -> assertEquals(BookingStatus.APPROVED.name(), bookings.get(0).getStatus()),
        () -> assertEquals(1L, bookings.get(1).getId()),
                () -> assertEquals(pastStartTime, bookings.get(1).getStart()),
                () -> assertEquals(pastEndTime, bookings.get(1).getEnd()),
                () -> assertEquals(1L, bookings.get(1).getItem().getId()),
                () -> assertEquals(BookingStatus.APPROVED.name(), bookings.get(1).getStatus())
        );
    }

    @Test
    public void getFutureBookingsByItemOwner() {
        given(bookingRepository.findFutureBookingByItemsOwner(user1.getId()))
                .willReturn(List.of(futureBooking));
        given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));
        given(itemRepository.countByOwnerId(user1.getId())).willReturn(1L);

        List<BookingDto> bookings = bookingService.getBookingItemOwner(user1.getId(), BookingState.FUTURE);

        assertAll(
                "Verify get Booking",
                () -> assertEquals(3L, bookings.get(0).getId()),
                () -> assertEquals(futureStartTime, bookings.get(0).getStart()),
                () -> assertEquals(futureEndTime, bookings.get(0).getEnd()),
                () -> assertEquals(1L, bookings.get(0).getItem().getId()),
                () -> assertEquals(BookingStatus.APPROVED.name(), bookings.get(0).getStatus())
        );
    }

    @Test
    public void getPastBookingsByItemOwner() {
        given(bookingRepository.findPastBookingByItemsOwner(user1.getId()))
                .willReturn(List.of(pastBooking));
        given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));
        given(itemRepository.countByOwnerId(user1.getId())).willReturn(1L);

        List<BookingDto> bookings = bookingService.getBookingItemOwner(user1.getId(), BookingState.PAST);

        assertAll(
                "Verify get Booking",
                () -> assertEquals(1L, bookings.get(0).getId()),
                () -> assertEquals(pastStartTime, bookings.get(0).getStart()),
                () -> assertEquals(pastEndTime, bookings.get(0).getEnd()),
                () -> assertEquals(1L, bookings.get(0).getItem().getId()),
                () -> assertEquals(BookingStatus.APPROVED.name(), bookings.get(0).getStatus())
        );
    }

    @Test
    public void getCurrentBookingsByItemOwner() {
        given(bookingRepository.findCurrentBookingByItemsOwner(user1.getId()))
                .willReturn(List.of(currentBooking));
        given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));
        given(itemRepository.countByOwnerId(user1.getId())).willReturn(1L);

        List<BookingDto> bookings = bookingService.getBookingItemOwner(user1.getId(), BookingState.CURRENT);

        assertAll(
                "Verify get Booking",
                () -> assertEquals(2L, bookings.get(0).getId()),
                () -> assertEquals(currentStartTime, bookings.get(0).getStart()),
                () -> assertEquals(currentEndTime, bookings.get(0).getEnd()),
                () -> assertEquals(2L, bookings.get(0).getItem().getId()),
                () -> assertEquals(BookingStatus.APPROVED.name(), bookings.get(0).getStatus())
        );
    }

    @Test
    public void getWaitingBookingsByItemOwner() {
        given(bookingRepository.findBookingByOwnerIdAndStatus(user3.getId(), BookingStatus.WAITING))
                .willReturn(List.of(waitingBooking));
        given(userRepository.findById(user3.getId())).willReturn(Optional.of(user3));
        given(itemRepository.countByOwnerId(user3.getId())).willReturn(1L);

        List<BookingDto> bookings = bookingService.getBookingItemOwner(user3.getId(), BookingState.WAITING);

        assertAll(
                "Verify get Booking",
                () -> assertEquals(4L, bookings.get(0).getId()),
                () -> assertEquals(futureStartTime, bookings.get(0).getStart()),
                () -> assertEquals(futureEndTime, bookings.get(0).getEnd()),
                () -> assertEquals(1L, bookings.get(0).getItem().getId()),
                () -> assertEquals(BookingStatus.WAITING.name(), bookings.get(0).getStatus())
        );
    }

    @Test
    public void getRejectedBookingsByItemOwner() {
        waitingBooking.setStatus(BookingStatus.REJECTED);

        given(bookingRepository.findBookingByOwnerIdAndStatus(user3.getId(), BookingStatus.REJECTED))
                .willReturn(List.of(waitingBooking));
        given(userRepository.findById(user3.getId())).willReturn(Optional.of(user3));
        given(itemRepository.countByOwnerId(user3.getId())).willReturn(1L);

        List<BookingDto> bookings = bookingService.getBookingItemOwner(user3.getId(), BookingState.REJECTED);

        assertAll(
                "Verify get Booking",
                () -> assertEquals(4L, bookings.get(0).getId()),
                () -> assertEquals(futureStartTime, bookings.get(0).getStart()),
                () -> assertEquals(futureEndTime, bookings.get(0).getEnd()),
                () -> assertEquals(1L, bookings.get(0).getItem().getId()),
                () -> assertEquals(BookingStatus.REJECTED.name(), bookings.get(0).getStatus())
        );
    }

    @Test
    public void getBookingsByItemOwnerWithoutItems() {
        given(userRepository.findById(user3.getId())).willReturn(Optional.of(user3));
        given(itemRepository.countByOwnerId(user3.getId())).willReturn(0L);

        assertThrowsExactly(NotFoundException.class, () -> bookingService.getBookingItemOwner(
                user3.getId(),
                BookingState.CURRENT)
        );
    }
}
