package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User create(User user);

    Optional<User> update(Long id, User user);

    void deleteById(Long id);

    List<User> getAll();

    Optional<User> getById(Long id);
}