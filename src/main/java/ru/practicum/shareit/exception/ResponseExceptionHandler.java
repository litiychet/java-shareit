package ru.practicum.shareit.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class ResponseExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected Map<String, String> notFoundExceptionHandler(NotFoundException e) {
        log.error("Status 404 Not Found {}\n{}", e.getMessage(), e.getStackTrace());
        return Map.of("exception", e.getClass().getName(), "error", e.getMessage());
    }

    @ExceptionHandler(NotOwnerException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    protected Map<String, String> notOwnerExceptionHandler(NotOwnerException e) {
        log.error("Status 403 Forbidden {}\n{}", e.getMessage(), e.getStackTrace());
        return Map.of("exception", e.getClass().getName(), "error", e.getMessage());
    }

    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    protected Map<String, String> duplicateEmailExceptionHandler(DuplicateEmailException e) {
        log.error("Status 409 Conflict {}\n{}", e.getMessage(), e.getStackTrace());
        return Map.of("exception", e.getClass().getName(), "error", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected Map<String, String> validationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.error("Status 400 Bad Request Validation Exception {}", errors);

        return errors;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected Map<String, String> enumConversionExceptionHandler(MethodArgumentTypeMismatchException e) {
        return Map.of("exception", e.getClass().getName(), "error", "Unknown state: " + e.getValue().toString());
    }

    @ExceptionHandler({
            MissingRequestHeaderException.class,
            MissingServletRequestParameterException.class,
            ItemUnvailableException.class,
            DateBookingException.class,
            AlreadyApprovedException.class,
            NotBookerException.class,
            ConstraintViolationException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected Map<String, String> notBookerExceptionHandler(Exception e) {
        log.error("Status 400 Bad Request {}\n{}", e.getMessage(), e.getStackTrace());
        return Map.of("exception", e.getClass().getName(), "error", e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected Map<String, String> runtimeExceptionHandler(RuntimeException e) {
        log.error("Status 500 Internal Server Error {}\n{}", e.getMessage(), e.getStackTrace());
        return Map.of("exception", e.getClass().toString(), "error", e.getMessage());
    }
}
