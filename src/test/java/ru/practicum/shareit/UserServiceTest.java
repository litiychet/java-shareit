package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user1;
    private User user2;

    @BeforeEach
    public void setup() {
        userRepository = Mockito.mock(UserRepository.class);

        userService = new UserServiceImpl(userRepository);

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
    }

    @Test
    public void createUserCorrect() {
        UserDto createUser = UserDto.builder()
                .name("user1")
                .email("user1@mail.ru")
                .build();

        given(userRepository.save(UserMapper.toUser(createUser))).willReturn(user1);

        UserDto responseDto = userService.create(createUser);

        assertAll(
                "Verify create User",
                () -> assertEquals(1L, responseDto.getId()),
                () -> assertEquals("user1", responseDto.getName()),
                () -> assertEquals("user1@mail.ru", responseDto.getEmail())
        );
    }

    @Test
    public void updateUserCorrect() {
        user1.setName("user1updated");
        user1.setEmail("user1updated@mail.ru");

        given(userRepository.save(user1)).willReturn(user1);
        given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));

        UserDto responseDto = userService.update(user1.getId(), UserMapper.toUserDto(user1));

        assertAll(
                "Verify update User",
                () -> assertEquals(1L, responseDto.getId()),
                () -> assertEquals("user1updated", responseDto.getName()),
                () -> assertEquals("user1updated@mail.ru", responseDto.getEmail())
        );
    }

    @Test
    public void updateNotExistsUser() {
        given(userRepository.findById(3L)).willReturn(Optional.empty());

        assertThrowsExactly(NotFoundException.class,
                () -> userService.update(3L, UserMapper.toUserDto(user1))
        );
    }

    @Test
    public void updateUserWithExistsEmail() {
        given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));
        given(userRepository.existsEmail(user2.getEmail())).willThrow(
                new DuplicateEmailException("Пользователь с таким email уже существует")
        );

        UserDto createUser = UserDto.builder()
                .id(1L)
                .name("user1")
                .email("user2@mail.ru")
                .build();

        assertThrowsExactly(DuplicateEmailException.class, () -> userService.update(user1.getId(), createUser));
    }

    @Test
    public void deleteNotExistsUser() {
        given(userRepository.findById(3L)).willReturn(Optional.empty());

        assertThrowsExactly(NotFoundException.class, () -> userService.delete(3L));
    }

    @Test
    public void getAllUsers() {
        given(userRepository.findAll()).willReturn(List.of(user1, user2));

        List<UserDto> responseDtos = userService.getAll();

        assertAll(
                "Verify get all Users",
                () -> assertEquals(1L, responseDtos.get(0).getId()),
                () -> assertEquals("user1", responseDtos.get(0).getName()),
                () -> assertEquals("user1@mail.ru", responseDtos.get(0).getEmail()),
                () -> assertEquals(2L, responseDtos.get(1).getId()),
                () -> assertEquals("user2", responseDtos.get(1).getName()),
                () -> assertEquals("user2@mail.ru", responseDtos.get(1).getEmail())
        );
    }

    @Test
    public void getUserById() {
        given(userRepository.findById(user2.getId())).willReturn(Optional.of(user2));

        UserDto responseDto = userService.getById(user2.getId());

        assertAll(
                "Verify get User by id",
                () -> assertEquals(2L, responseDto.getId()),
                () -> assertEquals("user2", responseDto.getName()),
                () -> assertEquals("user2@mail.ru", responseDto.getEmail())
        );
    }

    @Test
    public void getNotExistsUserById() {
        given(userRepository.findById(3L)).willReturn(Optional.empty());

        assertThrowsExactly(NotFoundException.class, () -> userService.getById(3L));
    }
}
