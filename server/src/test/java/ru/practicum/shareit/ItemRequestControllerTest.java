package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    private LocalDateTime currentTime = LocalDateTime.now();
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    public void setUp() {
        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("test request description")
                .created(currentTime)
                .build();
    }

    @Test
    public void createItemRequest() throws Exception {
        when(itemRequestService.createRequest(anyLong(), any())).thenReturn(itemRequestDto);

        ItemRequestCreateDto itemRequestCreateDto = ItemRequestCreateDto.builder()
                .description("test request description")
                .build();

        mvc.perform(post("/requests")
                    .header("X-Sharer-User-Id", 1L)
                    .content(mapper.writeValueAsString(itemRequestCreateDto))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @Test
    public void createItemRequestWithNotExistsUser() throws Exception {
        when(itemRequestService.createRequest(anyLong(), any())).thenThrow(new NotFoundException("test exception"));

        ItemRequestCreateDto itemRequestCreateDto = ItemRequestCreateDto.builder()
            .description("test request description")
            .build();

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemRequestCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404))
                .andExpect(result ->
                    assertThat(result.getResolvedException() instanceof NotFoundException, equalTo(true)));
    }

    @Test
    public void createItemRequestsWithBlankDescription() throws Exception {
        ItemRequestCreateDto itemRequestCreateDto = ItemRequestCreateDto.builder()
                .description("")
                .build();

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemRequestCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof MethodArgumentNotValidException, equalTo(true)));
    }

    @Test
    public void createItemRequestsWithBigDescription() throws Exception {
        ItemRequestCreateDto itemRequestCreateDto = ItemRequestCreateDto.builder()
                .description(new String(new char[257]).replace('\0', '1'))
                .build();

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemRequestCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof MethodArgumentNotValidException, equalTo(true)));
    }

    @Test
    public void getUserRequests() throws Exception {
        ItemForRequestDto item1 = ItemForRequestDto.builder()
                .id(1L)
                .name("test item1")
                .ownerId(1L)
                .build();

        ItemForRequestDto item2 = ItemForRequestDto.builder()
                .id(2L)
                .name("test item2")
                .ownerId(1L)
                .build();

        ItemRequestWithItemsDto itemRequest1 = ItemRequestWithItemsDto.builder()
                .description("test request1 description")
                .items(List.of(item1, item2))
                .created(currentTime)
                .build();

        ItemRequestWithItemsDto itemRequest2 = ItemRequestWithItemsDto.builder()
                .description("test request2 description")
                .items(List.of())
                .created(currentTime)
                .build();

        when(itemRequestService.getUserRequests(anyLong())).thenReturn(List.of(itemRequest1, itemRequest2));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[0].description", is(itemRequest1.getDescription())))
                .andExpect(jsonPath("$[0].items[0].name", is(itemRequest1.getItems().get(0).getName())))
                .andExpect(jsonPath("$[0].items[1].name", is(itemRequest1.getItems().get(1).getName())))
                .andExpect(jsonPath("$[1].description", is(itemRequest2.getDescription())));
    }

    @Test
    public void getItemRequestById() throws Exception {
        when(itemRequestService.getRequest(anyLong())).thenReturn(itemRequestDto);

        mvc.perform(get("/requests/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }
}
