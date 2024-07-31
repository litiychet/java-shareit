package ru.practicum.shareit;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:h2:mem:shareit",
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    @Test
    public void getUserItemsTest() {
        UserDto userDto1 = UserDto.builder()
                .name("test user1")
                .email("user1@test.ru")
                .build();

        UserDto userDto2 = UserDto.builder()
                .name("test user2")
                .email("user2@test.ru")
                .build();

        UserDto userDto3 = UserDto.builder()
                .name("test user3")
                .email("user3@test.ru")
                .build();

        ItemCreateDto itemCreateDto1 = ItemCreateDto.builder()
                .name("test item1")
                .description("test item1 description")
                .available(true)
                .build();

        ItemCreateDto itemCreateDto2 = ItemCreateDto.builder()
                .name("test item2")
                .description("test item2 description")
                .available(true)
                .build();

        UserDto createdUser1 = userService.create(userDto1);
        UserDto createdUser2 = userService.create(userDto2);
        UserDto createdUser3 = userService.create(userDto3);

        ItemCreateDto createdItem1 = itemService.create(createdUser1.getId(), itemCreateDto1);
        ItemCreateDto createdItem2 = itemService.create(createdUser1.getId(), itemCreateDto2);

        BookingCreateDto bookingCreateDto1 = BookingCreateDto.builder()
                .itemId(createdItem1.getId())
                .start(LocalDateTime.now().plus(1, ChronoUnit.DAYS))
                .end(LocalDateTime.now().plus(3, ChronoUnit.DAYS))
                .build();

        BookingCreateDto bookingCreateDto2 = BookingCreateDto.builder()
                .itemId(createdItem2.getId())
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plus(1, ChronoUnit.DAYS))
                .build();

        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
                .text("test comment from user3")
                .build();

        BookingDto createdBooking1 = bookingService.create(createdUser2.getId(), bookingCreateDto1);
        bookingService.changeStatus(createdUser1.getId(), createdBooking1.getId(), true);

        BookingDto createdBooking2 = bookingService.create(createdUser3.getId(), bookingCreateDto2);
        bookingService.changeStatus(createdUser1.getId(), createdBooking2.getId(), true);

        itemService.addComment(createdUser3.getId(), createdItem2.getId(), commentCreateDto);

        List<ItemResponseDto> userItems = itemService.getUserItems(createdUser1.getId());

        assertThat(userItems.get(0).getName(), equalTo("test item1"));
        assertThat(userItems.get(0).getDescription(), equalTo("test item1 description"));
        assertThat(userItems.get(0).getAvailable(), equalTo(true));
        assertThat(userItems.get(0).getNextBooking().getBookerId(), equalTo(createdUser2.getId()));

        assertThat(userItems.get(1).getName(), equalTo("test item2"));
        assertThat(userItems.get(1).getDescription(), equalTo("test item2 description"));
        assertThat(userItems.get(1).getAvailable(), equalTo(true));
        assertThat(userItems.get(1).getLastBooking().getBookerId(), equalTo(createdUser3.getId()));
        assertThat(userItems.get(1).getComments().get(0).getText(), equalTo("test comment from user3"));
        assertThat(userItems.get(1).getComments().get(0).getAuthorName(), equalTo("test user3"));
    }
}
