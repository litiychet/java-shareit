package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.IdFactory;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@Qualifier("UserRepositoryInMemory")
public class UserRepositoryInMemory implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final IdFactory idFactory = new IdFactory();

    @Override
    public User create(User user) {
        validateEmailExists(user);

        user.setId(idFactory.getId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> update(Long id, User user) {
        return users.values().stream()
                .filter(u -> u.getId().equals(id))
                .peek(u -> {
                    if (user.getName() != null)
                        u.setName(user.getName());
                    if (user.getEmail() != null)
                        if (!u.getEmail().equals(user.getEmail())) {
                            validateEmailExists(user);
                            u.setEmail(user.getEmail());
                        }
                })
                .findFirst();
    }

    @Override
    public void deleteById(Long id) {
        users.remove(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getById(Long id) {
        User user = users.get(id);
        return user != null ? Optional.of(user) : Optional.empty();
    }

    private void validateEmailExists(User user) {
        if (users.values().stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail())))
            throw new DuplicateEmailException("Пользователь с таким email уже существует");
    }
}