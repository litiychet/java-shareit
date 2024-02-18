package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(@Qualifier("inMemoryUserRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {
        return userRepository.create(user);
    }

    @Override
    public User update(Long id, User user) {
        validateExistsUser(id);
        return userRepository.update(id, user);
    }

    @Override
    public void delete(Long id) {
        validateExistsUser(id);
        userRepository.deleteById(id);
    }

    @Override
    public List<User> getAll() {
        return userRepository.getAll();
    }

    @Override
    public User getById(Long id) {
        validateExistsUser(id);
        return userRepository.getById(id);
    }

    private void validateExistsUser(Long id) {
        if (userRepository.getById(id) == null)
            throw new NotFoundException("Пользователя с ID " + id + " не найдено");
    }
}