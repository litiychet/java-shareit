package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemRepositoryInMemory;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ItemServiceTest {
    static UserRepository userRepository = new UserRepositoryInMemory();
    static UserService userService = new UserServiceImpl(userRepository);

    static ItemRepository itemRepository = new ItemRepositoryInMemory();
    static ItemService itemService = new ItemServiceImpl(itemRepository, userRepository);

    @BeforeAll
    public static void setUp() {
        UserDto user1 = UserDto.builder()
                .name("testuser1")
                .email("user1@test.ru")
                .build();

        UserDto user2 = UserDto.builder()
                .name("testuser2")
                .email("user2@test.ru")
                .build();

        userService.create(user1);
        userService.create(user2);

        ItemDto item1 = ItemDto.builder()
                .name("testitem1")
                .description("test item 1 description")
                .available(true)
                .build();

        ItemDto item2 = ItemDto.builder()
                .name("testitem2")
                .description("test item 2 description")
                .available(true)
                .build();

        itemService.create(1L, item1);
        itemService.create(2L, item1);
    }

    @Test
    public void create() {
        UserDto newUser = UserDto.builder()
                .name("testuser3")
                .email("user3@test.ru")
                .build();

        userService.create(newUser);

        ItemDto newItem = ItemDto.builder()
                .name("testitem3")
                .description("test item 3")
                .available(true)
                .build();

        ItemDto itemDto = itemService.create(3L, newItem);

        assertEquals(3L, itemDto.getId());
        assertEquals("testitem3", itemDto.getName());
        assertEquals("test item 3", itemDto.getDescription());
        assertEquals(true, itemDto.getAvailable());
    }

    @Test
    public void update() {
        ItemDto newItem = ItemDto.builder()
                .name("updateitem")
                .description("update test item description")
                .available(false)
                .build();

        ItemDto updatedItem = itemService.update(2L, 2L, newItem);

        assertEquals(2L, updatedItem.getId());
        assertEquals("updateitem", updatedItem.getName());
        assertEquals("update test item description", updatedItem.getDescription());
        assertEquals(false, updatedItem.getAvailable());
    }

    @Test
    public void get() {
        ItemDto item = itemService.get(1L);

        assertEquals(1L, item.getId());
        assertEquals("testitem1", item.getName());
        assertEquals("test item 1 description", item.getDescription());
        assertEquals(true, item.getAvailable());
    }

    @Test
    public void getUserItems() {
        List<ItemDto> items = itemService.getUserItems(1L);

        assertEquals(1, items.size());

        ItemDto item = items.get(0);

        assertEquals(1L, item.getId());
        assertEquals("testitem1", item.getName());
        assertEquals("test item 1 description", item.getDescription());
        assertEquals(true, item.getAvailable());
    }

    @Test
    public void searchItem() {
        List<ItemDto> items = itemService.searchItem("testitem1");

        assertEquals(1, items.size());

        ItemDto item1 = items.get(0);

        assertEquals(1L, item1.getId());
    }
}
