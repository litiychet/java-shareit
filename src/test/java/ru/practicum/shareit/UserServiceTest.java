package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    static UserRepository userRepository = new UserRepositoryInMemory();
    static UserService userService = new UserServiceImpl(userRepository);

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
    }

    @Test
    public void createUser() {
        User user3 = new User();
        user3.setName("testuser3");
        user3.setEmail("user3@test.ru");

        User createdUser = userService.create(user3);

        assertEquals(3L, createdUser.getId());
        assertEquals("testuser3", createdUser.getName());
        assertEquals("user3@test.ru", createdUser.getEmail());

        userService.delete(3L);
    }

    @Test
    public void updateUser() {
        User updateUser = new User();
        updateUser.setName("updateuser");
        updateUser.setEmail("update@test.ru");

        User newUser = userService.update(2L, updateUser);

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
        User user = userService.getById(1L);

        assertEquals(user.getId(), 1L);
        assertEquals(user.getName(), "testuser1");
        assertEquals(user.getEmail(), "user1@test.ru");
    }
}
