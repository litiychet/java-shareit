package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemValidationTest {
    @Mock
    static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validateEmptyNameInCreate() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("");
        itemDto.setDescription("test description");
        itemDto.setAvailable(true);

        Set<ConstraintViolation<ItemDto>> constraintViolations = validator.validate(itemDto, ValidateMarker.Create.class);
        for (ConstraintViolation<ItemDto> c : constraintViolations) {
            assertEquals("must not be empty", c.getMessage());
        }
    }

    @Test
    void validateEmptyDescriptionInCreate() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("testname");
        itemDto.setDescription("");
        itemDto.setAvailable(true);

        Set<ConstraintViolation<ItemDto>> constraintViolations = validator.validate(itemDto, ValidateMarker.Create.class);
        for (ConstraintViolation<ItemDto> c : constraintViolations) {
            assertEquals("must not be empty", c.getMessage());
        }
    }

    @Test
    void validateNullAvailableInCreate() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("testname");
        itemDto.setDescription("test description");

        Set<ConstraintViolation<ItemDto>> constraintViolations = validator.validate(itemDto, ValidateMarker.Create.class);
        for (ConstraintViolation<ItemDto> c : constraintViolations) {
            assertEquals("must not be null", c.getMessage());
        }
    }
}
