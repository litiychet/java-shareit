package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private ItemRequest itemRequest;
    private User user1;
    private LocalDateTime currentTime;

    @BeforeEach
    public void setup() {
        itemRepository = Mockito.mock(ItemRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        itemRequestRepository = Mockito.mock(ItemRequestRepository.class);

        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);

        currentTime = LocalDateTime.now();

        user1 = User.builder()
                .id(1L)
                .name("test user")
                .email("test@user.ru")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("test description")
                .requestor(user1)
                .created(currentTime)
                .build();
    }

    @Test
    public void createItemRequestCorrect() {
        ItemRequestCreateDto itemRequestCreateDto = ItemRequestCreateDto.builder()
                .description("test description")
                .build();

        ItemRequest createItemRequest = ItemRequest.builder()
                .description("test description")
                .requestor(user1)
                .created(currentTime)
                .build();

        given(itemRequestRepository.save(createItemRequest)).willReturn(itemRequest);
        given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));

        ItemRequestDto itemRequestDto = itemRequestService.createRequest(1L, itemRequestCreateDto);

        assertAll(
                () -> assertEquals(1L, itemRequestDto.getId()),
                () -> assertEquals("test description", itemRequestDto.getDescription()),
                () -> assertEquals(currentTime, itemRequestDto.getCreated())
        );
    }

    @Test
    public void getUsersRequestsWithItems() {
        User user2 = User.builder()
                .id(2L)
                .name("test user 2")
                .email("test2@user.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("test item")
                .description("test item")
                .owner(user2)
                .request(itemRequest)
                .available(true)
                .build();

        given(itemRequestRepository.findAllByRequestorId(user1.getId()))
                .willReturn(List.of(itemRequest));
        given(itemRepository.findAllByRequestIdIn(List.of(itemRequest.getId()))).willReturn(List.of(item));
        given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));

        List<ItemRequestWithItemsDto> itemRequestWithItemsDto = itemRequestService.getUserRequests(user1.getId());

        assertAll(
                () -> assertEquals("test description", itemRequestWithItemsDto.get(0).getDescription()),
                () -> assertEquals(currentTime, itemRequestWithItemsDto.get(0).getCreated()),
                () -> assertEquals(1L, itemRequestWithItemsDto.get(0).getItems().get(0).getId()),
                () -> assertEquals("test item", itemRequestWithItemsDto.get(0).getItems().get(0).getName()),
                () -> assertEquals(2L, itemRequestWithItemsDto.get(0).getItems().get(0).getOwnerId())
        );
    }

    @Test
    public void getRequestWithNotExistsUser() {
        assertThrowsExactly(NotFoundException.class,
                () -> itemRequestService.getUserRequests(1L)
        );
    }

    @Test
    public void getUserRequestsItemWithSort() {
        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("test1 description")
                .requestor(user1)
                .created(LocalDateTime.now().minus(3, ChronoUnit.DAYS))
                .build();

        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(2L)
                .description("test2 description")
                .requestor(user1)
                .created(LocalDateTime.now().minus(2, ChronoUnit.DAYS))
                .build();

        ItemRequest itemRequest3 = ItemRequest.builder()
                .id(3L)
                .description("test3 description")
                .requestor(user1)
                .created(LocalDateTime.now().minus(1, ChronoUnit.DAYS))
                .build();

        given(userRepository.findById(1L)).willReturn(Optional.of(user1));
        given(itemRequestRepository.findAllByRequestorId(1L)).willReturn(List.of(
                itemRequest2,
                itemRequest1,
                itemRequest3)
        );

        List<ItemRequestWithItemsDto> requestList = itemRequestService.getUserRequests(1L);

        assertAll(
                () -> assertEquals("test3 description", requestList.get(0).getDescription()),
                () -> assertEquals("test2 description", requestList.get(1).getDescription()),
                () -> assertEquals("test1 description", requestList.get(2).getDescription())
        );
    }

    @Test
    public void getUserEmptyRequests() {
        given(userRepository.findById(1L)).willReturn(Optional.of(user1));
        given(itemRequestRepository.findAllByRequestorId(1L)).willReturn(List.of());

        List<ItemRequestWithItemsDto> requests = itemRequestService.getUserRequests(1L);
        assertAll(
                () -> assertEquals(true, requests.isEmpty())
        );
    }

    @Test
    public void getRequestById() {
        given(itemRequestRepository.findById(1L)).willReturn(Optional.of(itemRequest));

        ItemRequestDto itemRequestDto = itemRequestService.getRequest(1L);

        assertAll(
                () -> assertEquals(1L, itemRequestDto.getId()),
                () -> assertEquals("test description", itemRequestDto.getDescription()),
                () -> assertEquals(currentTime, itemRequestDto.getCreated())
        );
    }

    @Test
    public void getRequestByIdNotFound() {
        assertThrowsExactly(NotFoundException.class,
                () -> itemRequestService.getRequest(1L)
        );
    }
}
