package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.IdFactory;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;

@Repository
@Qualifier("inMemoryUserRepository")
public class UserRepositoryInMemory implements UserRepository {
    private final List<User> users = new ArrayList<>();
    private final IdFactory idFactory = new IdFactory();

    @Override
    public User create(User user) {
        validateEmailExists(user);

        user.setId(idFactory.getId());
        users.add(user);
        return user;
    }

    @Override
    public User update(Long id, User user) {
        return users.stream()
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
                .findFirst()
                .orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        users.removeIf(u -> u.getId().equals(id));
    }

    @Override
    public List<User> getAll() {
        return users;
    }

    @Override
    public User getById(Long id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private void validateEmailExists(User user) {
        if (users.stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail())))
            throw new ValidationException("Пользователь с таким email уже существует");
    }
}