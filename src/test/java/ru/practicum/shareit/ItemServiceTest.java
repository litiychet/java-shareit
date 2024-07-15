package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotBookerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.*;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user1;
    private User user2;

    private Item item1;
    private Item item2;

    private LocalDateTime pastStartTime;
    private LocalDateTime pastEndTime;
    private LocalDateTime currentStartTime;
    private LocalDateTime currentEndTime;
    private LocalDateTime futureStartTime;
    private LocalDateTime futureEndTime;

    private LocalDateTime commentCreateDate;


    @BeforeEach
    public void setup() {
        itemRepository = Mockito.mock(ItemRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        bookingRepository = Mockito.mock(BookingRepository.class);
        commentRepository = Mockito.mock(CommentRepository.class);

        itemService = new ItemServiceImpl(itemRepository,
                userRepository,
                bookingRepository,
                commentRepository);

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

        commentCreateDate = LocalDateTime.now();

        user1 = User.builder()
                .id(1L)
                .name("user 1")
                .email("user1@mail.ru")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("user 2")
                .email("user2@mail.ru")
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
    }

    @Test
    public void createCorrect() {
        Item item1Create = Item.builder()
                .name("item 1")
                .description("item 1 description")
                .available(true)
                .owner(user1)
                .build();

        given(itemRepository.save(item1Create)).willReturn(item1);
        given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));

        ItemCreateDto responseDto = itemService.create(user1.getId(), ItemMapper.toItemDto(item1Create));

        assertAll("Verify Created Item",
                () -> assertEquals(1L, responseDto.getId()),
                () -> assertEquals("item 1", responseDto.getName()),
                () -> assertEquals("item 1 description", responseDto.getDescription()),
                () -> assertEquals(true, responseDto.getAvailable())
        );
    }

    @Test
    public void createWithNotExistsUser() {
        Item item1Create = Item.builder()
                .name("item 1")
                .description("item 1 description")
                .available(true)
                .owner(user1)
                .build();

        given(userRepository.findById(3L)).willReturn(Optional.empty());

        assertThrowsExactly(NotFoundException.class,
                () -> itemService.create(3L, ItemMapper.toItemDto(item1Create)));
    }

    @Test
    public void updateCorrect() {
        item1.setName("item 1 updated");
        item1.setDescription("item 1 description updated");
        item1.setAvailable(false);

        given(itemRepository.findById(item1.getId())).willReturn(Optional.of(item1));
        given(itemRepository.save(item1)).willReturn(item1);
        given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));

        ItemCreateDto responseDto = itemService.update(user1.getId(),
                item1.getId(),
                ItemMapper.toItemDto(item1)
        );

        assertAll(
                "Verify Update Item",
                () -> assertEquals(1L, responseDto.getId()),
                () -> assertEquals("item 1 updated", responseDto.getName()),
                () -> assertEquals("item 1 description updated", responseDto.getDescription()),
                () -> assertEquals(false, responseDto.getAvailable())
        );
    }

    @Test
    public void updateWithNotExistsUser() {
        given(itemRepository.findById(item1.getId())).willReturn(Optional.of(item1));
        given(userRepository.findById(3L)).willReturn(Optional.empty());

        assertThrowsExactly(NotFoundException.class,
                () -> itemService.update(3L,
                        item1.getId(),
                        ItemMapper.toItemDto(item1))
        );
    }

    @Test
    public void updateWithNotExistsItem() {
        given(itemRepository.findById(3L)).willReturn(Optional.empty());

        item1.setId(3L);
        assertThrowsExactly(NotFoundException.class,
                () -> itemService.update(user1.getId(), 3L, ItemMapper.toItemDto(item1))
        );
    }

    @Test
    public void updateWithNotOwnerUser() {
        given(itemRepository.findById(item1.getId())).willReturn(Optional.of(item1));
        given(userRepository.findById(user2.getId())).willReturn(Optional.of(user2));

        assertThrowsExactly(NotOwnerException.class,
                () -> itemService.update(user2.getId(), item1.getId(), ItemMapper.toItemDto(item1))
        );
    }

    @Test
    public void updateWithOneEmptyField() {
        Item updateItem = Item.builder()
                .id(1L)
                .name(null)
                .description("item 1 description updated")
                .available(null)
                .owner(null)
                .build();

        given(itemRepository.findById(item1.getId())).willReturn(Optional.of(item1));
        given(itemRepository.save(updateItem)).willReturn(item1);
        given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));

        ItemCreateDto responseDto = itemService.update(user1.getId(), item1.getId(), ItemMapper.toItemDto(updateItem));

        assertAll(
                "Verify update one field of Item",
                () -> assertEquals(1L, responseDto.getId()),
                () -> assertEquals("item 1", responseDto.getName()),
                () -> assertEquals("item 1 description updated", responseDto.getDescription()),
                () -> assertEquals(true, responseDto.getAvailable())
        );
    }

    @Test
    public void getCorrect() {
        given(itemRepository.findById(item2.getId())).willReturn(Optional.of(item2));
        given(userRepository.findById(user2.getId())).willReturn(Optional.of(user2));

        ItemResponseDto responseDto = itemService.get(item2.getId(), user2.getId());

        assertAll(
                "Verify get Item",
                () -> assertEquals(2L, responseDto.getId()),
                () -> assertEquals("item 2", responseDto.getName()),
                () -> assertEquals("item 2 description", responseDto.getDescription()),
                () -> assertEquals(false, responseDto.getAvailable())
        );
    }

    @Test
    public void getItemWithNotExistsItem() {
        given(itemRepository.findById(3L)).willReturn(Optional.empty());

        assertThrowsExactly(NotFoundException.class, () -> itemService.get(3L, user1.getId()));
    }

    @Test
    public void getItemWithNotExistsUser() {
        given(itemRepository.findById(item2.getId())).willReturn(Optional.of(item2));
        given(userRepository.findById(3L)).willReturn(Optional.empty());

        assertThrowsExactly(NotFoundException.class, () -> itemService.get(item2.getId(), 3L));
    }

    @Test
    public void getItemWithComments() {
        Comment comment1 = Comment.builder()
                .id(1L)
                .text("comment 1 for item 1")
                .item(item1)
                .author(user2)
                .created(commentCreateDate)
                .build();

        Comment comment2 = Comment.builder()
                .id(2L)
                .text("comment 2 for item 1")
                .item(item1)
                .author(user1)
                .created(commentCreateDate)
                .build();

        given(itemRepository.findById(item1.getId())).willReturn(Optional.of(item1));
        given(userRepository.findById(user2.getId())).willReturn(Optional.of(user2));
        given(commentRepository.findAllByItemId(item1.getId())).willReturn(List.of(comment1, comment2));

        ItemResponseDto responseDto = itemService.get(item1.getId(), user2.getId());

        assertAll(
                "Verify get Comment for Item",
                () -> assertEquals(1L, responseDto.getId()),
                () -> assertEquals("item 1", responseDto.getName()),
                () -> assertEquals("item 1 description", responseDto.getDescription()),
                () -> assertEquals(true, responseDto.getAvailable()),
                () -> assertEquals(2, responseDto.getComments().size()),
                () -> assertEquals(1L, responseDto.getComments().get(0).getId()),
                () -> assertEquals("comment 1 for item 1", responseDto.getComments().get(0).getText()),
                () -> assertEquals(user2.getName(), responseDto.getComments().get(0).getAuthorName()),
                () -> assertEquals(commentCreateDate,
                        responseDto.getComments().get(0).getCreated()),
                () -> assertEquals(2L, responseDto.getComments().get(1).getId()),
                () -> assertEquals("comment 2 for item 1", responseDto.getComments().get(1).getText()),
                () -> assertEquals(user1.getName(), responseDto.getComments().get(1).getAuthorName()),
                () -> assertEquals(commentCreateDate,
                        responseDto.getComments().get(1).getCreated())

        );
    }

    @Test
    public void getItemByNotOwner() {
        given(itemRepository.findById(item1.getId())).willReturn(Optional.of(item1));
        given(userRepository.findById(user2.getId())).willReturn(Optional.of(user2));

        ItemResponseDto responseDto = itemService.get(item1.getId(), user2.getId());

        assertAll(
                "Verify get Item by not Owner",
                () -> assertEquals(1L, responseDto.getId()),
                () -> assertEquals("item 1", responseDto.getName()),
                () -> assertEquals("item 1 description", responseDto.getDescription()),
                () -> assertEquals(true, responseDto.getAvailable()),
                () -> assertNull(responseDto.getLastBooking()),
                () -> assertNull(responseDto.getNextBooking())
        );
    }

    @Test
    public void getItemByOwner() {
        Booking lastBooking = Booking.builder()
                .id(1L)
                .start(pastStartTime)
                .end(pastEndTime)
                .booker(user1)
                .item(item2)
                .status(BookingStatus.APPROVED)
                .build();

        Booking nextBooking = Booking.builder()
                .id(2L)
                .start(futureStartTime)
                .end(futureEndTime)
                .booker(user1)
                .item(item2)
                .status(BookingStatus.APPROVED)
                .build();

        item2.setAvailable(true);

        given(itemRepository.findById(item2.getId())).willReturn(Optional.of(item2));
        given(userRepository.findById(user2.getId())).willReturn(Optional.of(user2));
        given(bookingRepository.findLastBookingByItemId(item2.getId())).willReturn(List.of(lastBooking));
        given(bookingRepository.findNextBookingByItemId(item2.getId())).willReturn(List.of(nextBooking));

        ItemResponseDto responseDto = itemService.get(item2.getId(), user2.getId());

        assertAll(
                "Verify get Item by Owner",
                () -> assertEquals(2L, responseDto.getId()),
                () -> assertEquals("item 2", responseDto.getName()),
                () -> assertEquals("item 2 description", responseDto.getDescription()),
                () -> assertEquals(true, responseDto.getAvailable()),
                () -> assertEquals(1L, responseDto.getLastBooking().getId()),
                () -> assertEquals(1L, responseDto.getLastBooking().getBookerId()),
                () -> assertEquals(2L, responseDto.getNextBooking().getId()),
                () -> assertEquals(1L, responseDto.getNextBooking().getBookerId())
        );
    }

    @Test
    public void getUserItems() {
        Comment comment1 = Comment.builder()
                .id(1L)
                .text("comment 1 for item 1")
                .item(item1)
                .author(user2)
                .created(commentCreateDate)
                .build();

        Comment comment2 = Comment.builder()
                .id(2L)
                .text("comment 2 for item 1")
                .item(item1)
                .author(user1)
                .created(commentCreateDate)
                .build();

        Booking lastBooking = Booking.builder()
                .id(1L)
                .start(pastStartTime)
                .end(pastEndTime)
                .booker(user1)
                .item(item1)
                .status(BookingStatus.APPROVED)
                .build();

        Booking nextBooking = Booking.builder()
                .id(2L)
                .start(futureStartTime)
                .end(futureEndTime)
                .booker(user1)
                .item(item1)
                .status(BookingStatus.APPROVED)
                .build();

        Item item3 = Item.builder()
                .id(3L)
                .name("item 3")
                .description("item 3 description")
                .available(true)
                .owner(user1)
                .build();

        given(itemRepository.findAllByOwnerId(user1.getId())).willReturn(List.of(item1, item3));
        given(commentRepository.findByItemsIdIn(List.of(item1.getId(), item3.getId()))).willReturn(List.of(comment1, comment2));
        given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));
        given(bookingRepository.findLastBookingByItemIn(List.of(item1.getId(), item3.getId()))).willReturn(List.of(lastBooking));
        given(bookingRepository.findNextBookingByItemIn(List.of(item1.getId(), item3.getId()))).willReturn(List.of(nextBooking));

        List<ItemResponseDto> responseDtos = itemService.getUserItems(user1.getId());

        assertAll(
                "Verify get all users Items",
                () -> assertEquals(1L, responseDtos.get(0).getId()),
                () -> assertEquals("item 1", responseDtos.get(0).getName()),
                () -> assertEquals("item 1 description", responseDtos.get(0).getDescription()),
                () -> assertEquals(true, responseDtos.get(0).getAvailable()),
                () -> assertEquals(2, responseDtos.get(0).getComments().size()),
                () -> assertEquals("comment 1 for item 1", responseDtos.get(0).getComments().get(0).getText()),
                () -> assertEquals("comment 2 for item 1", responseDtos.get(0).getComments().get(1).getText()),
                () -> assertEquals(1L, responseDtos.get(0).getLastBooking().getId()),
                () -> assertEquals(2L, responseDtos.get(0).getNextBooking().getId()),
                () -> assertEquals(3L, responseDtos.get(1).getId()),
                () -> assertEquals("item 3", responseDtos.get(1).getName()),
                () -> assertEquals("item 3 description", responseDtos.get(1).getDescription()),
                () -> assertEquals(List.of(), responseDtos.get(1).getComments()),
                () -> assertNull(responseDtos.get(1).getLastBooking()),
                () -> assertNull(responseDtos.get(1).getNextBooking())
        );
    }

    @Test
    public void getNotExistsUserItems() {
        given(userRepository.findById(3L)).willReturn(Optional.empty());

        assertThrowsExactly(NotFoundException.class, () -> itemService.getUserItems(3L));
    }

    @Test
    public void addCommentCorrect() {
        CommentCreateDto createComment1 = CommentCreateDto.builder()
                .text("comment 1 for item 1")
                .build();

        Comment comment1 = Comment.builder()
                .id(1L)
                .text("comment 1 for item 1")
                .item(item1)
                .author(user2)
                .created(commentCreateDate)
                .build();

        Booking pastBooking = Booking.builder()
                .id(1L)
                .start(pastStartTime)
                .end(pastEndTime)
                .booker(user2)
                .item(item1)
                .status(BookingStatus.APPROVED)
                .build();

        Booking currentBooking = Booking.builder()
                .id(2L)
                .start(currentStartTime)
                .end(currentEndTime)
                .booker(user2)
                .item(item1)
                .status(BookingStatus.APPROVED)
                .build();

        given(userRepository.findById(user2.getId())).willReturn(Optional.of(user2));
        given(itemRepository.findById(item1.getId())).willReturn(Optional.of(item2));
        given(commentRepository.save(CommentMapper.toComment(createComment1))).willReturn(comment1);
        given(bookingRepository.existsCurrentAndPastBookingByUserId(user2.getId())).willReturn(true);

        CommentResponseDto responseDto = itemService.addComment(user2.getId(), item1.getId(), createComment1);

        assertAll(
                "Verify add Comment to Item",
                () -> assertEquals(1L, responseDto.getId()),
                () -> assertEquals("comment 1 for item 1", responseDto.getText()),
                () -> assertEquals("user 2", responseDto.getAuthorName()),
                () -> assertEquals(commentCreateDate,
                        responseDto.getCreated())
        );
    }

    @Test
    public void addCommentWithNotExistsUser() {
        CommentCreateDto createComment1 = CommentCreateDto.builder()
                .text("comment 1 for item 1")
                .build();

        given(userRepository.findById(3L)).willReturn(Optional.empty());

        assertThrowsExactly(NotFoundException.class,
                () -> itemService.addComment(3L, item1.getId(), createComment1)
        );
    }

    @Test
    public void addCommentToNotExistsItem() {
        CommentCreateDto createComment1 = CommentCreateDto.builder()
                .text("comment 1 for item 1")
                .build();

        given(itemRepository.findById(3L)).willReturn(Optional.empty());
        given(userRepository.findById(user2.getId())).willReturn(Optional.of(user2));

        assertThrowsExactly(NotFoundException.class,
                () -> itemService.addComment(user2.getId(), 3L, createComment1)
        );
    }

    @Test
    public void addCommentWithUserNotBooker() {
        CommentCreateDto createComment1 = CommentCreateDto.builder()
                .text("comment 1 for item 1")
                .build();

        given(itemRepository.findById(item1.getId())).willReturn(Optional.of(item1));
        given(userRepository.findById(user2.getId())).willReturn(Optional.of(user2));
        given(bookingRepository.existsCurrentAndPastBookingByUserId(user2.getId())).willReturn(false);

        assertThrowsExactly(NotBookerException.class,
                () -> itemService.addComment(user2.getId(), item1.getId(), createComment1)
        );
    }

    @Test
    public void searchItem() {
        given(itemRepository.findByNameOrDescriptionLike("item")).willReturn(List.of(item1, item2));

        List<ItemCreateDto> responseDtos = itemService.searchItem("item");

        assertAll(
                "Verify search Item",
                () -> assertEquals("item 1", responseDtos.get(0).getName()),
                () -> assertEquals("item 2", responseDtos.get(1).getName())
        );
    }
}
