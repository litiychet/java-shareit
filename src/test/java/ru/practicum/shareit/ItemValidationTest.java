package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ru.practicum.shareit.item.dto.ItemCreateDto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemValidationTest {
    @Mock
    static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validateEmptyNameInCreate() {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("")
                .description("test description")
                .available(true)
                .build();

        Set<ConstraintViolation<ItemCreateDto>> constraintViolations = validator.validate(itemCreateDto, ValidateMarker.Create.class);
        for (ConstraintViolation<ItemCreateDto> c : constraintViolations) {
            assertEquals("must not be empty", c.getMessage());
        }
    }

    @Test
    void validateEmptyDescriptionInCreate() {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("testname")
                .description("")
                .available(true)
                .build();

        Set<ConstraintViolation<ItemCreateDto>> constraintViolations = validator.validate(itemCreateDto, ValidateMarker.Create.class);
        for (ConstraintViolation<ItemCreateDto> c : constraintViolations) {
            assertEquals("must not be empty", c.getMessage());
        }
    }

    @Test
    void validateNullAvailableInCreate() {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("testname")
                .description("test description")
                .build();

        Set<ConstraintViolation<ItemCreateDto>> constraintViolations = validator.validate(itemCreateDto, ValidateMarker.Create.class);
        for (ConstraintViolation<ItemCreateDto> c : constraintViolations) {
            assertEquals("must not be null", c.getMessage());
        }
    }
}
