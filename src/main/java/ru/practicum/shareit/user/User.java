package ru.practicum.shareit.user;

import lombok.Data;
import ru.practicum.shareit.ValidateMarker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class User {
    private Long id;
    @NotEmpty(groups = ValidateMarker.Create.class)
    private String name;
    @Email(groups = {ValidateMarker.Create.class, ValidateMarker.Update.class})
    @NotEmpty(groups = ValidateMarker.Create.class)
    private String email;
}
