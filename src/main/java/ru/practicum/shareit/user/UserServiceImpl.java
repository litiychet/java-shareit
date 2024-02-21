package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(@Qualifier("UserRepositoryInMemory") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto create(UserDto user) {
        return UserMapper.toUserDto(
                userRepository.create(UserMapper.toUser(user))
        );
    }

    @Override
    public UserDto update(Long id, UserDto user) {
        validateExistsUser(id);
        return UserMapper.toUserDto(
                userRepository.update(id, UserMapper.toUser(user)).get()
        );
    }

    @Override
    public void delete(Long id) {
        validateExistsUser(id);
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long id) {
        validateExistsUser(id);
        return UserMapper.toUserDto(
                userRepository.getById(id).get()
        );
    }

    private void validateExistsUser(Long id) {
        if (userRepository.getById(id).isEmpty())
            throw new NotFoundException("Пользователя с ID " + id + " не найдено");
    }
}