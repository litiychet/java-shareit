package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.ValidateMarker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class UserDto {
    private Long id;
    @NotEmpty(groups = ValidateMarker.Create.class)
    private String name;
    @Email(groups = {ValidateMarker.Create.class, ValidateMarker.Update.class})
    @NotEmpty(groups = ValidateMarker.Create.class)
    private String email;
}
