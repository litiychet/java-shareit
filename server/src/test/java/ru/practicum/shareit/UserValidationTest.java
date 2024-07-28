package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ru.practicum.shareit.ValidateMarker;
import ru.practicum.shareit.user.model.User;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserValidationTest {
    @Mock
    static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validateIncorrectEmail() {
        User user = User.builder()
                .name("testuser")
                .email("testuserru")
                .build();

        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user, ValidateMarker.Create.class, ValidateMarker.Update.class);
        for (ConstraintViolation<User> c : constraintViolations) {
            assertEquals("must be a well-formed email address", c.getMessage());
        }
    }

    @Test
    void validateEmptyEmailInCreate() {
        User user = User.builder()
                .name("testuser")
                .email("")
                .build();

        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user, ValidateMarker.Create.class);
        for (ConstraintViolation<User> c : constraintViolations) {
            assertEquals("must not be empty", c.getMessage());
        }
    }

    @Test
    void validateEmptyNameInCreate() {
        User user = User.builder()
                .name("")
                .email("test@user.ru")
                .build();

        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user, ValidateMarker.Create.class);
        for (ConstraintViolation<User> c : constraintViolations) {
            assertEquals("must not be empty", c.getMessage());
        }
    }
}
