package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {
    User create(User user);

    User update(Long id, User user);

    void deleteById(Long id);

    List<User> getAll();

    User getById(Long id);
}