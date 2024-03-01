package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.*;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    static UserRepository userRepository = new UserRepositoryInMemory();
    static UserService userService = new UserServiceImpl(userRepository);

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
    }

    @Test
    public void createUser() {
        UserDto user3 = UserDto.builder()
                .name("testuser3")
                .email("user3@test.ru")
                .build();

        UserDto createdUser = userService.create(user3);

        assertEquals(3L, createdUser.getId());
        assertEquals("testuser3", createdUser.getName());
        assertEquals("user3@test.ru", createdUser.getEmail());

        userService.delete(3L);
    }

    @Test
    public void updateUser() {
        UserDto updateUser = UserDto.builder()
                .name("updateuser")
                .email("update@test.ru")
                .build();

        UserDto newUser = userService.update(2L, updateUser);

        assertEquals(2L, newUser.getId());
        assertEquals("updateuser", newUser.getName());
        assertEquals("update@test.ru", newUser.getEmail());
    }

    @Test
    public void deleteUser() {
        assertEquals(userService.getAll().size(), 2);
        userService.delete(2L);
        assertEquals(userService.getAll().size(), 1);
    }

    @Test
    public void getAll() {
        assertEquals(userService.getAll().size(), 2);
    }

    @Test
    public void getUserById() {
        UserDto user = userService.getById(1L);

        assertEquals(user.getId(), 1L);
        assertEquals(user.getName(), "testuser1");
        assertEquals(user.getEmail(), "user1@test.ru");
    }
}
