package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.ValidateMarker;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingCreateDto {
    @NotNull(groups = ValidateMarker.Create.class)
    private LocalDateTime start;

    @NotNull(groups = ValidateMarker.Create.class)
    private LocalDateTime end;

    @NotNull(groups = ValidateMarker.Create.class)
    private Long itemId;
}
