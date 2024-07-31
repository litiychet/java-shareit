package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    private UserDto userDto = UserDto.builder()
            .id(1L)
            .name("test user1")
            .email("user1@test.ru")
            .build();

    @Test
    public void createUser() throws Exception {
        when(userService.create(any())).thenReturn(userDto);

        mvc.perform(post("/users")
                    .content(mapper.writeValueAsString(userDto))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    public void createUserWithDuplicateEmail() throws Exception {
        when(userService.create(any())).thenThrow(new DuplicateEmailException("test exception"));

        mvc.perform(post("/users")
                    .content(mapper.writeValueAsString(userDto))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(409))
                .andExpect(result ->
                        assertThat(result.getResolvedException().getMessage(), equalTo("test exception")))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof DuplicateEmailException, equalTo(true)));
    }

    @Test
    public void createUserWithEmptyName() throws Exception {
        userDto.setName("");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof MethodArgumentNotValidException, equalTo(true)));
    }

    @Test
    public void createUserWithEmptyEmail() throws Exception {
        userDto.setEmail("");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof MethodArgumentNotValidException, equalTo(true)));
    }

    @Test
    public void createUserWithBigName() throws Exception {
        userDto.setName(new String(new char[65]).replace('\0', '1'));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof MethodArgumentNotValidException, equalTo(true)));
    }

    @Test
    public void createUserWithBigEmail() throws Exception {
        userDto.setEmail(new String(new char[65]).replace('\0', '1'));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof MethodArgumentNotValidException, equalTo(true)));
    }

    @Test
    public void createUserWithIncorrectEmail() throws Exception {
        userDto.setEmail("test user email");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof MethodArgumentNotValidException, equalTo(true)));
    }

    @Test
    public void updateUser() throws Exception {
        UserDto updateUser = UserDto.builder()
                .name("test update user1")
                .build();

        userDto.setName("test update user1");

        when(userService.update(anyLong(), any())).thenReturn(userDto);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(updateUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    public void updateNotExistsUser() throws Exception {
        UserDto updateUser = UserDto.builder()
                .name("test update user1")
                .build();

        when(userService.update(anyLong(), any())).thenThrow(new NotFoundException("test exception"));

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(updateUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404))
                .andExpect(result ->
                        assertThat(result.getResolvedException().getMessage(), equalTo("test exception")))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof NotFoundException, equalTo(true)));
    }

    @Test
    public void updateUserWithDuplicateEmail() throws Exception {
        UserDto updateUser = UserDto.builder()
                .email("user1@test.ru")
                .build();

        when(userService.update(anyLong(), any())).thenThrow(new DuplicateEmailException("test exception"));

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(updateUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(409))
                .andExpect(result ->
                        assertThat(result.getResolvedException().getMessage(), equalTo("test exception")))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof DuplicateEmailException, equalTo(true)));
    }

    @Test
    public void getAllUsers() throws Exception {
        UserDto userDto1 = UserDto.builder()
                .id(2L)
                .name("test user2")
                .email("user2@test.ru")
                .build();

        when(userService.getAll()).thenReturn(List.of(userDto, userDto1));

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())))
                .andExpect(jsonPath("$[1].id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(userDto1.getName())))
                .andExpect(jsonPath("$[1].email", is(userDto1.getEmail())));
    }

    @Test
    public void getUserById() throws Exception {
        when(userService.getById(any())).thenReturn(userDto);

        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    public void getNotExistsUserById() throws Exception {
        when(userService.getById(any())).thenThrow(new NotFoundException("test exception"));

        mvc.perform(get("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404))
                .andExpect(result ->
                        assertThat(result.getResolvedException().getMessage(), equalTo("test exception")))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof NotFoundException, equalTo(true)));
    }
}