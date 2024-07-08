package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.DateBookingException;
import ru.practicum.shareit.exception.ItemUnvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

        assertThrowsExactly(NotFoundException.class, () -> bookingService.create(4L, createBookingDto));
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

        createBookingDto.setStart(LocalDateTime.now().minus(1, ChronoUnit.DAYS));
        createBookingDto.setEnd(LocalDateTime.now().plus(1, ChronoUnit.DAYS));

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
}
