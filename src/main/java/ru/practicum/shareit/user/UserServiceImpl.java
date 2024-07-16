package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto user) {
        return UserMapper.toUserDto(
                userRepository.save(UserMapper.toUser(user))
        );
    }

    @Override
    public UserDto update(Long id, UserDto user) {
        User newUser = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Пользователя с ID " + id + " не найдено")
        );

        if (user.getName() != null)
            newUser.setName(user.getName());
        if (user.getEmail() != null)
            if (!user.getEmail().equals(newUser.getEmail())) {
                if (userRepository.existsEmail(user.getEmail()))
                    throw new DuplicateEmailException("Пользователь с таким email уже существует");
                newUser.setEmail(user.getEmail());
            }

        return UserMapper.toUserDto(userRepository.save(newUser));
    }

    @Override
    public void delete(Long id) {
        userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Пользователя с ID " + id + " не найдено")
        );

        userRepository.deleteById(id);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Пользователя с ID " + id + " не найдено")
        );

        return UserMapper.toUserDto(user);
    }
}