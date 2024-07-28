package ru.practicum.shareit;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:h2:mem:shareit",
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {
    private final EntityManager em;
    private final UserService userService;

    @Test
    public void getAllUsers() {
        UserDto userDto1 = UserDto.builder()
                .name("test user1")
                .email("user1@test.ru")
                .build();

        UserDto userDto2 = UserDto.builder()
                .name("test user2")
                .email("user2@test.ru")
                .build();

        userService.create(userDto1);
        userService.create(userDto2);

        List<UserDto> users = userService.getAll();

        assertThat(users.get(0).getName(), equalTo(userDto1.getName()));
        assertThat(users.get(0).getEmail(), equalTo(userDto1.getEmail()));

        assertThat(users.get(1).getName(), equalTo(userDto2.getName()));
        assertThat(users.get(1).getEmail(), equalTo(userDto2.getEmail()));
    }
}
