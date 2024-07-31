package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HeaderName;
import ru.practicum.shareit.ValidateMarker;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader(HeaderName.USER_ID) final long userId,
                             @Validated(ValidateMarker.Create.class) @RequestBody BookingCreateDto bookingCreateDto) {
        log.info("POST /bookings {} {}: {}", bookingCreateDto, HeaderName.USER_ID, userId);
        return bookingService.create(userId, bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto changeStatus(@RequestHeader(HeaderName.USER_ID) final long userId,
                                   @PathVariable Long bookingId,
                                   @RequestParam Boolean approved) {
        log.info("PATCH /bookings/{}?approved={} {}: {}", bookingId, approved.toString(), HeaderName.USER_ID, userId);
        return bookingService.changeStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(@RequestHeader(HeaderName.USER_ID) final long userId,
                          @PathVariable Long bookingId) {
        log.info("GET /bookings/{} {}: {}", bookingId, HeaderName.USER_ID, userId);
        return bookingService.get(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getByState(@RequestHeader(HeaderName.USER_ID) final long userId,
                                       @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("GET /bookings?state={} {}: {}", state.name(), HeaderName.USER_ID, userId);
        return bookingService.getBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getByItemsOwner(@RequestHeader(HeaderName.USER_ID) final long ownerId,
                                            @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("GET /bookings/owner?state={} {}: {}", state.name(), HeaderName.USER_ID, ownerId);
        return bookingService.getBookingItemOwner(ownerId, state);
    }
}
