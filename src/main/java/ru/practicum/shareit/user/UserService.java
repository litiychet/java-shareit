package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto user);

    UserDto update(Long id, UserDto user);

    void delete(Long id);

    List<UserDto> getAll();

    UserDto getById(Long id);
}
