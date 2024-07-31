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
import ru.practicum.shareit.exception.NotBookerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private ItemCreateDto itemCreateDto;

    @BeforeEach
    public void setUp() {
        itemCreateDto = ItemCreateDto.builder()
                .id(1L)
                .name("test item1")
                .description("test item1 description")
                .available(true)
                .build();
    }

    @Test
    public void createItem() throws Exception {
        when(itemService.create(anyLong(), any())).thenReturn(itemCreateDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(itemCreateDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemCreateDto.getName())))
                .andExpect(jsonPath("$.description", is(itemCreateDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemCreateDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemCreateDto.getRequestId())));
    }

    @Test
    public void createItemWithEmptyName() throws Exception {
        itemCreateDto.setName("");

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof MethodArgumentNotValidException, equalTo(true)));
    }

    @Test
    public void createItemWithBigName() throws Exception {
        itemCreateDto.setName(new String(new char[65]).replace('\0', '1'));

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof MethodArgumentNotValidException, equalTo(true)));
    }

    @Test
    public void createItemWithEmptyDescription() throws Exception {
        itemCreateDto.setDescription("");

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof MethodArgumentNotValidException, equalTo(true)));
    }

    @Test
    public void createItemWithBigDescription() throws Exception {
        itemCreateDto.setDescription(new String(new char[257]).replace('\0', '1'));

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof MethodArgumentNotValidException, equalTo(true)));
    }

    @Test
    public void createItemWithNullAvailable() throws Exception {
        itemCreateDto.setAvailable(null);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof MethodArgumentNotValidException, equalTo(true)));
    }

    @Test
    public void updateItemWithEmptyName() throws Exception {
        itemCreateDto.setName("");

        when(itemService.update(anyLong(), anyLong(), any())).thenReturn(itemCreateDto);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(itemCreateDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemCreateDto.getName())))
                .andExpect(jsonPath("$.description", is(itemCreateDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemCreateDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemCreateDto.getRequestId())));
    }

    @Test
    public void updateItemWithEmptyDescription() throws Exception {
        itemCreateDto.setDescription("");

        when(itemService.update(anyLong(), anyLong(), any())).thenReturn(itemCreateDto);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(itemCreateDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemCreateDto.getName())))
                .andExpect(jsonPath("$.description", is(itemCreateDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemCreateDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemCreateDto.getRequestId())));
    }

    @Test
    public void updateItemWithNullAvailable() throws Exception {
        itemCreateDto.setAvailable(null);

        when(itemService.update(anyLong(), anyLong(), any())).thenReturn(itemCreateDto);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(itemCreateDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemCreateDto.getName())))
                .andExpect(jsonPath("$.description", is(itemCreateDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemCreateDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemCreateDto.getRequestId())));
    }

    @Test
    public void getItemById() throws Exception {
        ItemResponseDto itemResponseDto = ItemResponseDto.builder()
                .id(itemCreateDto.getId())
                .name(itemCreateDto.getName())
                .description(itemCreateDto.getDescription())
                .available(itemCreateDto.getAvailable())
                .build();

        when(itemService.get(anyLong(), anyLong())).thenReturn(itemResponseDto);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(itemCreateDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemCreateDto.getName())))
                .andExpect(jsonPath("$.description", is(itemCreateDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemCreateDto.getAvailable())));
    }

    @Test
    public void getItemByIdWithNotExistsUser() throws Exception {
        when(itemService.get(anyLong(), anyLong())).thenThrow(new NotFoundException("test exception"));

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof NotFoundException, equalTo(true)));
    }

    @Test
    public void getUserItems() throws Exception {
        ItemResponseDto itemResponseDto1 = ItemResponseDto.builder()
                .id(itemCreateDto.getId())
                .name(itemCreateDto.getName())
                .description(itemCreateDto.getDescription())
                .available(itemCreateDto.getAvailable())
                .build();

        ItemResponseDto itemResponseDto2 = ItemResponseDto.builder()
                .id(2L)
                .name("test item2")
                .description("test item2 description")
                .available(true)
                .build();

        when(itemService.getUserItems(anyLong())).thenReturn(List.of(itemResponseDto1, itemResponseDto2));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[0].id", is(itemResponseDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemResponseDto1.getName())))
                .andExpect(jsonPath("$[0].description", is(itemResponseDto1.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemResponseDto1.getAvailable())))
                .andExpect(jsonPath("$[1].id", is(itemResponseDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(itemResponseDto2.getName())))
                .andExpect(jsonPath("$[1].description", is(itemResponseDto2.getDescription())))
                .andExpect(jsonPath("$[1].available", is(itemResponseDto2.getAvailable())));
    }

    @Test
    public void getNotExistsUserItems() throws Exception {
        when(itemService.getUserItems(anyLong())).thenThrow(new NotFoundException("test exception"));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof NotFoundException, equalTo(true)));
    }

    @Test
    public void searchItemsByText() throws Exception {
        ItemCreateDto itemCreateDto2 = ItemCreateDto.builder()
                .id(2L)
                .name("test item2")
                .description("test item2 description")
                .available(true)
                .build();

        when(itemService.searchItem(anyString())).thenReturn(List.of(itemCreateDto, itemCreateDto2));

        mvc.perform(get("/items/search?text=test")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[0].id", is(itemCreateDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemCreateDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemCreateDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemCreateDto.getAvailable())))
                .andExpect(jsonPath("$[1].id", is(itemCreateDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(itemCreateDto2.getName())))
                .andExpect(jsonPath("$[1].description", is(itemCreateDto2.getDescription())))
                .andExpect(jsonPath("$[1].available", is(itemCreateDto2.getAvailable())));
    }

    @Test
    public void searchItemsWithEmptyText() throws Exception {
        mvc.perform(get("/items/search?text=")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void addCommentToItem() throws Exception {
        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
                .text("test comment")
                .build();

        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                .id(1L)
                .text("test comment")
                .created(LocalDateTime.now())
                .authorName("test user1")
                .build();

        when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(commentResponseDto);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(commentResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentResponseDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentResponseDto.getAuthorName())));
    }

    @Test
    public void addEmptyCommentToItem() throws Exception {
        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
                .text("")
                .build();

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof MethodArgumentNotValidException, equalTo(true)));
    }

    @Test
    public void addBigCommentToItem() throws Exception {
        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
                .text(new String(new char[257]).replace('\0', '1'))
                .build();

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof MethodArgumentNotValidException, equalTo(true)));
    }

    @Test
    public void addCommentWithNotExistsUser() throws Exception {
        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
                .text("test comment")
                .build();

        when(itemService.addComment(anyLong(), anyLong(), any())).thenThrow(new NotFoundException("test exception"));

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof NotFoundException, equalTo(true)));
    }

    @Test
    public void addCommentWithNotOwnerItem() throws Exception {
        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
                .text("test comment")
                .build();

        when(itemService.addComment(anyLong(), anyLong(), any())).thenThrow(new NotBookerException("test exception"));

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof NotBookerException, equalTo(true)));
    }
}