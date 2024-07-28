package ru.practicum.shareit;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = {"jdbc.url=jdbc:h2:mem:shareit", "spring.jpa.show-sql=true" },
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {
    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;

    @Test
    public void getDifferentUserBookings() {
        UserDto userDto1 = UserDto.builder()
                .name("test user1")
                .email("user1@test.ru")
                .build();

        UserDto userDto2 = UserDto.builder()
                .name("test user2")
                .email("user2@test.ru")
                .build();

        ItemCreateDto itemCreateDto1 = ItemCreateDto.builder()
                .name("test item1")
                .description("test item1 description")
                .available(true)
                .build();

        UserDto createdUser1 = userService.create(userDto1);
        UserDto createdUser2 = userService.create(userDto2);

        ItemCreateDto createdItem = itemService.create(createdUser1.getId(), itemCreateDto1);

        BookingCreateDto bookingCreateDto1 = BookingCreateDto.builder()
                .start(LocalDateTime.now().minus(3, ChronoUnit.DAYS))
                .end(LocalDateTime.now().minus(1, ChronoUnit.DAYS))
                .itemId(createdItem.getId())
                .build();

        BookingCreateDto bookingCreateDto2 = BookingCreateDto.builder()
                .start(LocalDateTime.now().minus(1, ChronoUnit.DAYS))
                .end(LocalDateTime.now().plus(1, ChronoUnit.DAYS))
                .itemId(createdItem.getId())
                .build();

        BookingCreateDto bookingCreateDto3 = BookingCreateDto.builder()
                .start(LocalDateTime.now().plus(3, ChronoUnit.DAYS))
                .end(LocalDateTime.now().plus(5, ChronoUnit.DAYS))
                .itemId(createdItem.getId())
                .build();

        BookingCreateDto bookingCreateDto4 = BookingCreateDto.builder()
                .start(LocalDateTime.now().minus(3, ChronoUnit.DAYS))
                .end(LocalDateTime.now().plus(5, ChronoUnit.DAYS))
                .itemId(createdItem.getId())
                .build();

        BookingCreateDto bookingCreateDto5 = BookingCreateDto.builder()
                .start(LocalDateTime.now().minus(3, ChronoUnit.DAYS))
                .end(LocalDateTime.now().plus(5, ChronoUnit.DAYS))
                .itemId(createdItem.getId())
                .build();

        BookingDto createdBooking1 = bookingService.create(createdUser2.getId(), bookingCreateDto1);
        BookingDto createdBooking2 = bookingService.create(createdUser2.getId(), bookingCreateDto2);
        BookingDto createdBooking3 = bookingService.create(createdUser2.getId(), bookingCreateDto3);
        BookingDto createdBooking4 = bookingService.create(createdUser2.getId(), bookingCreateDto4);
        BookingDto createdBooking5 = bookingService.create(createdUser2.getId(), bookingCreateDto5);

        bookingService.changeStatus(createdUser1.getId(), createdBooking1.getId(), true);
        bookingService.changeStatus(createdUser1.getId(), createdBooking2.getId(), true);
        bookingService.changeStatus(createdUser1.getId(), createdBooking3.getId(), true);
        bookingService.changeStatus(createdUser1.getId(), createdBooking4.getId(), false);

        List<BookingDto> allBookings = bookingService.getBookings(createdUser2.getId(), BookingState.ALL);
        List<BookingDto> currentBookings = bookingService.getBookings(createdUser2.getId(), BookingState.CURRENT);
        List<BookingDto> pastBookings = bookingService.getBookings(createdUser2.getId(), BookingState.PAST);
        List<BookingDto> futureBookings = bookingService.getBookings(createdUser2.getId(), BookingState.FUTURE);
        List<BookingDto> rejectedBookings = bookingService.getBookings(createdUser2.getId(), BookingState.REJECTED);
        List<BookingDto> waitingBookings = bookingService.getBookings(createdUser2.getId(), BookingState.WAITING);

        assertThat(allBookings.size(), equalTo(5));

        assertThat(currentBookings.size(), equalTo(3));

        assertThat(pastBookings.size(), equalTo(1));

        assertThat(futureBookings.size(), equalTo(1));

        assertThat(rejectedBookings.size(), equalTo(1));

        assertThat(waitingBookings.size(), equalTo(1));
    }
}
