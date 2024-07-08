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
        validateExistsUser(id);

        User newUser = userRepository.findById(id).get();

        if (user.getName() != null)
            newUser.setName(user.getName());
        if (user.getEmail() != null)
            if (!user.getEmail().equals(newUser.getEmail())) {
                validateExistsEmail(user.getEmail());
                newUser.setEmail(user.getEmail());
            }

        return UserMapper.toUserDto(userRepository.save(newUser));
    }

    @Override
    public void delete(Long id) {
        validateExistsUser(id);
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
        validateExistsUser(id);
        return UserMapper.toUserDto(
                userRepository.findById(id).get()
        );
    }

    private void validateExistsUser(Long id) {
        if (userRepository.findById(id).isEmpty())
            throw new NotFoundException("Пользователя с ID " + id + " не найдено");
    }

    private void validateExistsEmail(String email) {
        if (userRepository.findByEmail(email).isPresent())
            throw new DuplicateEmailException("Пользователь с таким email уже существует");
    }
}