package ru.practicum.shareit;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ru.practicum.shareit.ValidateMarker;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemRequestValidationTest {
    @Mock
    static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validateEmptyDescriptionItemRequest() {
        ItemRequestCreateDto itemRequestCreateDto = ItemRequestCreateDto.builder()
                .description("")
                .build();

        Set<ConstraintViolation<ItemRequestCreateDto>> constraintViolations = validator.validate(
                itemRequestCreateDto,
                ValidateMarker.Create.class
        );
        for (ConstraintViolation<ItemRequestCreateDto> c : constraintViolations) {
            assertEquals("must not be blank", c.getMessage());
        }
    }

    @Test
    void validateSizeDescriptionItemRequest() {
        String description = new String(new char[257]).replace('\0', '1');
        ItemRequestCreateDto itemRequestCreateDto = ItemRequestCreateDto.builder()
                .description(description)
                .build();

        Set<ConstraintViolation<ItemRequestCreateDto>> constraintViolations = validator.validate(
                itemRequestCreateDto,
                ValidateMarker.Create.class
        );
        for (ConstraintViolation<ItemRequestCreateDto> c : constraintViolations) {
            assertEquals("size must be between 0 and 256", c.getMessage());
        }
    }
}