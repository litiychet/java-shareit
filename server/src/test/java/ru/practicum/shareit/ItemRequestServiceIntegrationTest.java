package ru.practicum.shareit;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = {"jdbc.url=jdbc:h2:mem:shareit", "spring.jpa.show-sql=true" },
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceIntegrationTest {
    private final ItemRequestService itemRequestService;
    private final ItemService itemService;
    private final UserService userService;

    @Test
    public void getUserRequestsWithItems() {
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
                .requestId(1L)
                .build();

        ItemCreateDto itemCreateDto2 = ItemCreateDto.builder()
                .name("test item2")
                .description("test item2 description")
                .available(true)
                .requestId(2L)
                .build();

        ItemCreateDto itemCreateDto3 = ItemCreateDto.builder()
                .name("test item3")
                .description("test item3 description")
                .available(true)
                .requestId(2L)
                .build();

        ItemRequestCreateDto itemRequestCreateDto1 = ItemRequestCreateDto.builder()
                .description("test request1 description")
                .build();

        ItemRequestCreateDto itemRequestCreateDto2 = ItemRequestCreateDto.builder()
                .description("test request2 description")
                .build();

        UserDto createdUser1 = userService.create(userDto1);
        UserDto createdUser2 = userService.create(userDto2);

        itemRequestService.createRequest(createdUser2.getId(), itemRequestCreateDto1);
        itemRequestService.createRequest(createdUser2.getId(), itemRequestCreateDto2);

        itemService.create(createdUser1.getId(), itemCreateDto1);
        itemService.create(createdUser1.getId(), itemCreateDto2);
        itemService.create(createdUser1.getId(), itemCreateDto3);

        List<ItemRequestWithItemsDto> itemRequests = itemRequestService.getUserRequests(createdUser2.getId());

        assertThat(itemRequests.get(0).getDescription(), equalTo(itemRequestCreateDto2.getDescription()));
        assertThat(itemRequests.get(0).getItems().get(0).getName(), equalTo(itemCreateDto2.getName()));
        assertThat(itemRequests.get(0).getItems().get(1).getName(), equalTo(itemCreateDto3.getName()));

        assertThat(itemRequests.get(1).getDescription(), equalTo(itemRequestCreateDto1.getDescription()));
        assertThat(itemRequests.get(1).getItems().get(0).getName(), equalTo(itemCreateDto1.getName()));
    }
}
