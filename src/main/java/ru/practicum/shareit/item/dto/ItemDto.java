package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.ValidateMarker;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemDto {
    private Long id;
    @NotEmpty(groups = ValidateMarker.Create.class)
    private String name;
    @NotEmpty(groups = ValidateMarker.Create.class)
    private String description;
    @NotNull(groups = ValidateMarker.Create.class)
    private Boolean available;
}
