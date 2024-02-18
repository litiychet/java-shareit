package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.ValidateMarker;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User create(@Validated(ValidateMarker.Create.class) @RequestBody User user) {
        return userService.create(user);
    }

    @PatchMapping("/{userId}")
    public User update(@PathVariable Long userId,
                       @Validated(ValidateMarker.Update.class) @RequestBody User user) {
        return userService.update(userId, user);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }

    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public User getById(@PathVariable Long userId) {
        return userService.getById(userId);
    }
}
