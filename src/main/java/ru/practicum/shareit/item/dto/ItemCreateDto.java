package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.ValidateMarker;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
public class ItemCreateDto {
    private Long id;

    @NotEmpty(groups = ValidateMarker.Create.class)
    private String name;

    @NotEmpty(groups = ValidateMarker.Create.class)
    private String description;

    @NotNull(groups = ValidateMarker.Create.class)
    private Boolean available;
}
