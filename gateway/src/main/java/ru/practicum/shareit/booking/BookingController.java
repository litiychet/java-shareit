package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.HeaderName;
import ru.practicum.shareit.ValidateMarker;


@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HeaderName.USER_ID) final long userId,
                             @Validated(ValidateMarker.Create.class) @RequestBody BookingCreateDto bookingCreateDto) {
        log.info("POST /bookings {} {}: {}", bookingCreateDto, HeaderName.USER_ID, userId);
        return bookingClient.create(userId, bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> changeStatus(@RequestHeader(HeaderName.USER_ID) final long userId,
                                   @PathVariable Long bookingId,
                                   @RequestParam Boolean approved) {
        log.info("PATCH /bookings/{}?approved={} {}: {}", bookingId, approved.toString(), HeaderName.USER_ID, userId);
        return bookingClient.changeStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> get(@RequestHeader(HeaderName.USER_ID) final long userId,
                          @PathVariable Long bookingId) {
        log.info("GET /bookings/{} {}: {}", bookingId, HeaderName.USER_ID, userId);
        return bookingClient.get(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getByState(@RequestHeader(HeaderName.USER_ID) final long userId,
                                       @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("GET /bookings?state={} {}: {}", state.name(), HeaderName.USER_ID, userId);
        return bookingClient.getBookings(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getByItemsOwner(@RequestHeader(HeaderName.USER_ID) final long ownerId,
                                                  @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("GET /bookings/owner?state={} {}: {}", state.name(), HeaderName.USER_ID, ownerId);
        return bookingClient.getBookingItemOwner(ownerId, state);
    }
}