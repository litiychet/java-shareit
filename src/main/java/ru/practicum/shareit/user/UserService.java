package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    User create(User user);

    User update(Long id, User user);

    void delete(Long id);

    List<User> getAll();

    User getById(Long id);
}
