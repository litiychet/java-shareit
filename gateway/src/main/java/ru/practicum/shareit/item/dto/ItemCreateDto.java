package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.ValidateMarker;

@Data
@Builder
public class ItemCreateDto {
    private Long id;

    @NotEmpty(groups = ValidateMarker.Create.class)
    @Size(max = 64, groups = {ValidateMarker.Create.class, ValidateMarker.Update.class})
    private String name;

    @NotEmpty(groups = ValidateMarker.Create.class)
    @Size(max = 256, groups = {ValidateMarker.Create.class, ValidateMarker.Update.class})
    private String description;

    @NotNull(groups = ValidateMarker.Create.class)
    private Boolean available;

    private Long requestId;
}
