package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
    public BookingDto create(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                             @Validated(ValidateMarker.Create.class) @RequestBody BookingCreateDto bookingCreateDto) {
        log.info("POST /bookings {} X-Sharer-User-Id: {}", bookingCreateDto, userId);
        return bookingService.create(userId, bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto changeStatus(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                   @PathVariable Long bookingId,
                                   @RequestParam Boolean approved) {
        log.info("PATCH /bookings/{}?approved={} X-Sharer-User-Id: {}", bookingId, approved.toString(), userId);
        return bookingService.changeStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                          @PathVariable Long bookingId) {
        log.info("GET /bookings/{} X-Sharer-User-Id: {}", bookingId, userId);
        return bookingService.get(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getByState(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                       @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
        log.info("GET /bookings?state={} X-Sharer-User-Id: {}", state.name(), userId);
        return bookingService.getBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getByItemsOwner(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
                                            @RequestParam(required = false,
                                                    defaultValue = "ALL") BookingState state) {
        log.info("GET /bookings/owner?state={} X-Sharer-User-Id: {}", state.name(), ownerId);
        return bookingService.getBookingItemOwner(ownerId, state);
    }
}
