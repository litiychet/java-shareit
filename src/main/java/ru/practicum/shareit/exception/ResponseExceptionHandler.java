package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(annotations = RestController.class)
public class ResponseExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected Map<String, String> notFoundExceptionHandler(NotFoundException e) {
        return Map.of("exception", e.getClass().getName(), "error", e.getMessage());
    }

    @ExceptionHandler(NotOwnerException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    protected Map<String, String> notOwnerExceptionHandler(NotOwnerException e) {
        return Map.of("exception", e.getClass().getName(), "error", e.getMessage());
    }

    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    protected Map<String, String> duplicateEmailExceptionHandler(DuplicateEmailException e) {
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

        return errors;
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected Map<String, String> requestHeaderExceptionHandler(MissingRequestHeaderException e) {
        return Map.of("exception", e.getClass().getName(), "error", e.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected Map<String, String> requestHeaderExceptionHandler(MissingServletRequestParameterException e) {
        return Map.of("exception", e.getClass().getName(), "error", e.getMessage());
    }

    @ExceptionHandler(ItemUnvailableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected Map<String, String> itemUnvailableExceptionHandler(ItemUnvailableException e) {
        return Map.of("exception", e.getClass().getName(), "error", e.getMessage());
    }

    @ExceptionHandler(DateBookingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected Map<String, String> dateBookingExceptionHandler(DateBookingException e) {
        return Map.of("exception", e.getClass().getName(), "error", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected Map<String, String> enumConversionExceptionHandler(MethodArgumentTypeMismatchException e) {
        return Map.of("exception", e.getClass().getName(), "error", "Unknown state: " + e.getValue().toString());
    }

    @ExceptionHandler(AlreadyApprovedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected Map<String, String> alreadyApprovedExceptionHandler(AlreadyApprovedException e) {
        return Map.of("exception", e.getClass().getName(), "error", e.getMessage());
    }

    @ExceptionHandler(NotBookerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected Map<String, String> notBookerExceptionHandler(NotBookerException e) {
        return Map.of("exception", e.getClass().getName(), "error", e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected Map<String, String> runtimeExceptionHandler(RuntimeException e) {
        return Map.of("exception", e.getClass().toString(), "error", e.getMessage());
    }
}
