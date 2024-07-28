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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.AlreadyApprovedException;
import ru.practicum.shareit.exception.NotFoundException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void createBooking() throws Exception {
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plus(1, ChronoUnit.DAYS))
                .end(LocalDateTime.now().plus(2, ChronoUnit.DAYS))
                .itemId(1L)
                .build();

        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(bookingCreateDto.getStart())
                .end(bookingCreateDto.getEnd())
                .status(BookingStatus.WAITING.name())
                .build();

        when(bookingService.create(anyLong(), any())).thenReturn(bookingDto);


        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus())));
    }

    @Test
    public void createBookingWithNullStartDate() throws Exception {
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(null)
                .end(LocalDateTime.now().plus(3, ChronoUnit.DAYS))
                .itemId(1L)
                .build();

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof MethodArgumentNotValidException, equalTo(true)));

    }

    @Test
    public void createBookingWithNullEndDate() throws Exception {
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plus(2, ChronoUnit.DAYS))
                .end(null)
                .itemId(1L)
                .build();

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof MethodArgumentNotValidException, equalTo(true)));
    }

    @Test
    public void createBookingWithNullItemId() throws Exception {
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plus(2, ChronoUnit.DAYS))
                .end(LocalDateTime.now().plus(3, ChronoUnit.DAYS))
                .itemId(null)
                .build();

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof MethodArgumentNotValidException, equalTo(true)));
    }

    @Test
    public void createBookingWithPastStartDate() throws Exception {
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().minus(2, ChronoUnit.DAYS))
                .end(LocalDateTime.now().plus(3, ChronoUnit.DAYS))
                .itemId(1L)
                .build();

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof MethodArgumentNotValidException, equalTo(true)));
    }

    @Test
    public void createBookingWithPastEndDate() throws Exception {
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plus(2, ChronoUnit.DAYS))
                .end(LocalDateTime.now().minus(3, ChronoUnit.DAYS))
                .itemId(1L)
                .build();

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof MethodArgumentNotValidException, equalTo(true)));
    }

    @Test
    public void createBookingWithEqualStartAndEndDate() throws Exception {
        LocalDateTime currentTime = LocalDateTime.now();
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(currentTime)
                .end(currentTime)
                .itemId(1L)
                .build();

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof MethodArgumentNotValidException, equalTo(true)));
    }

    @Test
    public void changeBookingStatusToApproved() throws Exception {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plus(2, ChronoUnit.DAYS))
                .end(LocalDateTime.now().plus(3, ChronoUnit.DAYS))
                .status("APPROVED")
                .build();

        when(bookingService.changeStatus(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus())));
    }

    @Test
    public void changeBookingStatusToReject() throws Exception {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plus(2, ChronoUnit.DAYS))
                .end(LocalDateTime.now().plus(3, ChronoUnit.DAYS))
                .status("REJECTED")
                .build();

        when(bookingService.changeStatus(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mvc.perform(patch("/bookings/1?approved=false")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus())));
    }

    @Test
    public void changeBookingStatusToAlreadyApproved() throws Exception {
        when(bookingService.changeStatus(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new AlreadyApprovedException("test exception"));

        mvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof AlreadyApprovedException, equalTo(true)));
    }

    @Test
    public void changeBookingStatusWithNotExistsUserOrNotOwner() throws Exception {
        when(bookingService.changeStatus(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new NotFoundException("test exception"));

        mvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof NotFoundException, equalTo(true)));
    }

    @Test
    public void getBookingByOwner() throws Exception {
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plus(1, ChronoUnit.DAYS))
                .end(LocalDateTime.now().plus(2, ChronoUnit.DAYS))
                .itemId(1L)
                .build();

        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(bookingCreateDto.getStart())
                .end(bookingCreateDto.getEnd())
                .status(BookingStatus.APPROVED.name())
                .build();

        when(bookingService.get(anyLong(), anyLong())).thenReturn(bookingDto);


        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus())));
    }

    @Test
    public void getBookingByNotOwnerOrNotExistsUser() throws Exception {
        when(bookingService.get(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("test exception"));

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof NotFoundException, equalTo(true)));
    }

    @Test
    public void getBookingByStateFuture() throws Exception {
        BookingDto bookingDto1 = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now().now().plus(2, ChronoUnit.DAYS))
                .end(LocalDateTime.now().now().plus(3, ChronoUnit.DAYS))
                .status(BookingStatus.APPROVED.name())
                .build();

        BookingDto bookingDto2 = BookingDto.builder()
                .id(2L)
                .start(LocalDateTime.now().now().plus(5, ChronoUnit.DAYS))
                .end(LocalDateTime.now().now().plus(7, ChronoUnit.DAYS))
                .status(BookingStatus.APPROVED.name())
                .build();

        when(bookingService.getBookings(anyLong(), any())).thenReturn(List.of(bookingDto1, bookingDto2));

        mvc.perform(get("/bookings?state=FUTURE")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[0].id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto2.getStatus())))
                .andExpect(jsonPath("$[1].id", is(bookingDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].status", is(bookingDto2.getStatus())));
    }

    @Test
    public void getBookingByIncorrectState() throws Exception {
        mvc.perform(get("/bookings?state=STATE")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof MethodArgumentTypeMismatchException, equalTo(true)));
    }

    @Test
    public void getBookingByItemOwner() throws Exception {
        BookingDto bookingDto1 = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now().now().plus(2, ChronoUnit.DAYS))
                .end(LocalDateTime.now().now().plus(3, ChronoUnit.DAYS))
                .status(BookingStatus.APPROVED.name())
                .build();

        BookingDto bookingDto2 = BookingDto.builder()
                .id(2L)
                .start(LocalDateTime.now().now().plus(5, ChronoUnit.DAYS))
                .end(LocalDateTime.now().now().plus(7, ChronoUnit.DAYS))
                .status(BookingStatus.APPROVED.name())
                .build();

        when(bookingService.getBookingItemOwner(anyLong(), any())).thenReturn(List.of(bookingDto1, bookingDto2));

        mvc.perform(get("/bookings/owner?state=FUTURE")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[0].id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto2.getStatus())))
                .andExpect(jsonPath("$[1].id", is(bookingDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].status", is(bookingDto2.getStatus())));
    }

    @Test
    public void getBookingByItemOwnerWithIncorrectState() throws Exception {
        mvc.perform(get("/bookings/owner?state=STATE")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof MethodArgumentTypeMismatchException, equalTo(true)));
    }

    @Test
    public void getBookingByNotExistsItemOwner() throws Exception {
        when(bookingService.getBookingItemOwner(anyLong(), any())).thenThrow(new NotFoundException("test exception"));

        mvc.perform(get("/bookings/owner?state=ALL")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404))
                .andExpect(result ->
                        assertThat(result.getResolvedException() instanceof NotFoundException, equalTo(true)));
    }
}
