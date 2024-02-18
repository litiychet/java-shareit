package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.ValidateMarker;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ItemDto {
    private Long id;
    @NotEmpty(groups = {ValidateMarker.Create.class})
    private String name;
    @NotEmpty(groups = {ValidateMarker.Create.class})
    private String description;
    @NotNull(groups = {ValidateMarker.Create.class})
    private Boolean available;
}
