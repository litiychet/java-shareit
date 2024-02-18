package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemRepositoryInMemory;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ItemServiceTest {
    static UserRepository userRepository = new UserRepositoryInMemory();
    static UserService userService = new UserServiceImpl(userRepository);

    static ItemRepository itemRepository = new ItemRepositoryInMemory(userRepository);
    static ItemService itemService = new ItemServiceImpl(itemRepository);

    @BeforeAll
    public static void setUp() {
        User user1 = new User();
        user1.setName("testuser1");
        user1.setEmail("user1@test.ru");

        User user2 = new User();
        user2.setName("testuser2");
        user2.setEmail("user2@test.ru");

        userService.create(user1);
        userService.create(user2);

        ItemDto item1 = new ItemDto();
        item1.setName("testitem1");
        item1.setDescription("test item 1 description");
        item1.setAvailable(true);

        ItemDto item2 = new ItemDto();
        item2.setName("testitem2");
        item2.setDescription("test item 2 description");
        item2.setAvailable(true);

        itemService.create(1L, item1);
        itemService.create(2L, item1);
    }

    @Test
    public void create() {
        User newUser = new User();
        newUser.setName("testuser3");
        newUser.setEmail("user3@test.ru");

        userService.create(newUser);

        ItemDto newItem = new ItemDto();
        newItem.setName("testitem3");
        newItem.setDescription("test item 3");
        newItem.setAvailable(true);

        ItemDto itemDto = itemService.create(3L, newItem);

        assertEquals(3L, itemDto.getId());
        assertEquals("testitem3", itemDto.getName());
        assertEquals("test item 3", itemDto.getDescription());
        assertEquals(true, itemDto.getAvailable());
    }

    @Test
    public void update() {
        ItemDto newItem = new ItemDto();
        newItem.setName("updateitem");
        newItem.setDescription("update test item description");
        newItem.setAvailable(false);

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

    @Test
    public void searchItemWithEmptyText() {
        List<ItemDto> items = itemService.searchItem("");

        assertEquals(0, items.size());
    }
}
