package ru.practicum.shareit.booking;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exception.DateBookingException;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(final long userId, BookingCreateDto bookingCreateDto) {
        if (!bookingCreateDto.getEnd().isAfter(bookingCreateDto.getStart()))
            throw new DateBookingException("Некорректные даты бронированя");
        return post("", userId, bookingCreateDto);
    }

    public ResponseEntity<Object> changeStatus(final long userId,
                                               Long bookingId,
                                               Boolean approved) {
        Map<String, Object> parameters = Map.of(
                "approved", approved
        );
        StringBuilder path = new StringBuilder("/").append(bookingId).append("?approved={approved}");
        return patch(path.toString(), userId, parameters, null);
    }

    public ResponseEntity<Object> get(final long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getBookings(final long userId, BookingState state) {
        Map<String, Object> parameters = Map.of(
                "state", state.name()
        );
        StringBuilder path = new StringBuilder("?state={state}");
        return get(path.toString(), userId, parameters);
    }

    public ResponseEntity<Object> getBookingItemOwner(final long ownerId, BookingState state) {
        Map<String, Object> parameters = Map.of(
                "state", state.name()
        );
        StringBuilder path = new StringBuilder("/owner").append("?state={state}");
        return get(path.toString(), ownerId, parameters);
    }
}