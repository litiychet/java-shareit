package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.ValidateMarker;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingCreateDto {
    @NotNull(groups = ValidateMarker.Create.class)
    @FutureOrPresent(groups = ValidateMarker.Create.class)
    private LocalDateTime start;

    @NotNull(groups = ValidateMarker.Create.class)
    @Future(groups = ValidateMarker.Create.class)
    private LocalDateTime end;

    @NotNull(groups = ValidateMarker.Create.class)
    private Long itemId;
}
