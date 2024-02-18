package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ru.practicum.shareit.user.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserValidationTest {
    @Mock
    static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validateIncorrectEmail() {
        User user = new User();
        user.setName("testuser");
        user.setEmail("testuserru");

        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user, ValidateMarker.Create.class, ValidateMarker.Update.class);
        for (ConstraintViolation<User> c : constraintViolations) {
            assertEquals("must be a well-formed email address", c.getMessage());
        }
    }

    @Test
    void validateEmptyEmailInCreate() {
        User user = new User();
        user.setName("testuser");
        user.setEmail("");

        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user, ValidateMarker.Create.class);
        for (ConstraintViolation<User> c : constraintViolations) {
            assertEquals("must not be empty", c.getMessage());
        }
    }

    @Test
    void validateEmptyNameInCreate() {
        User user = new User();
        user.setName("");
        user.setEmail("test@user.ru");

        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user, ValidateMarker.Create.class);
        for (ConstraintViolation<User> c : constraintViolations) {
            assertEquals("must not be empty", c.getMessage());
        }
    }
}
