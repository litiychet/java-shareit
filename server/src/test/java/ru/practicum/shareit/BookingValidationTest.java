package ru.practicum.shareit;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ru.practicum.shareit.ValidateMarker;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingValidationTest {
    @Mock
    static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validateNullStartDate() {
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .end(LocalDateTime.now().plus(2, ChronoUnit.DAYS))
                .itemId(1L)
                .build();

        Set<ConstraintViolation<BookingCreateDto>> constraintViolations = validator.validate(
                bookingCreateDto,
                ValidateMarker.Create.class
        );
        for (ConstraintViolation<BookingCreateDto> c : constraintViolations) {
            assertEquals("must not be null", c.getMessage());
        }
    }

    @Test
    void validateFutureOrPresentStartDate() {
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().minus(2, ChronoUnit.DAYS))
                .end(LocalDateTime.now().plus(2, ChronoUnit.DAYS))
                .itemId(1L)
                .build();

        Set<ConstraintViolation<BookingCreateDto>> constraintViolations = validator.validate(
                bookingCreateDto,
                ValidateMarker.Create.class
        );
        for (ConstraintViolation<BookingCreateDto> c : constraintViolations) {
            assertEquals("must be a date in the present or in the future", c.getMessage());
        }
    }

    @Test
    void validateNullEndDate() {
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plus(2, ChronoUnit.DAYS))
                .itemId(1L)
                .build();

        Set<ConstraintViolation<BookingCreateDto>> constraintViolations = validator.validate(
                bookingCreateDto,
                ValidateMarker.Create.class
        );
        for (ConstraintViolation<BookingCreateDto> c : constraintViolations) {
            assertEquals("must not be null", c.getMessage());
        }
    }

    @Test
    void validateFutureEndDate() {
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plus(2, ChronoUnit.DAYS))
                .end(LocalDateTime.now().minus(1, ChronoUnit.DAYS))
                .itemId(1L)
                .build();

        Set<ConstraintViolation<BookingCreateDto>> constraintViolations = validator.validate(
                bookingCreateDto,
                ValidateMarker.Create.class
        );
        for (ConstraintViolation<BookingCreateDto> c : constraintViolations) {
            assertEquals("must be a future date", c.getMessage());
        }
    }

    @Test
    void validateNullItemId() {
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plus(2, ChronoUnit.DAYS))
                .end(LocalDateTime.now().plus(3, ChronoUnit.DAYS))
                .build();

        Set<ConstraintViolation<BookingCreateDto>> constraintViolations = validator.validate(
                bookingCreateDto,
                ValidateMarker.Create.class
        );
        for (ConstraintViolation<BookingCreateDto> c : constraintViolations) {
            assertEquals("must not be null", c.getMessage());
        }
    }
}
