package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.ValidateMarker;

@Data
@Builder
public class UserDto {
    private Long id;

    @NotEmpty(groups = ValidateMarker.Create.class)
    @Size(max = 64, groups = {ValidateMarker.Create.class, ValidateMarker.Update.class})
    private String name;

    @Email(groups = {ValidateMarker.Create.class, ValidateMarker.Update.class})
    @NotEmpty(groups = ValidateMarker.Create.class)
    @Size(max = 64, groups = {ValidateMarker.Create.class, ValidateMarker.Update.class})
    private String email;
}
