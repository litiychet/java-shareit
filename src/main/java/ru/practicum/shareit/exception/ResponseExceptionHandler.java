package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@RestControllerAdvice(annotations = RestController.class)
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected Map<String, String> notFoundExceptionHandler(NotFoundException e) {
        return Map.of("exception", "NotFoundException", "error", e.getMessage());
    }

    @ExceptionHandler(value = NotOwnerException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    protected Map<String, String> notOwnerExceptionHandler(NotOwnerException e) {
        return Map.of("exception", "NotOwnerException", "error", e.getMessage());
    }

    @ExceptionHandler(value = DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    protected Map<String, String> duplicateEmailExceptionHandler(DuplicateEmailException e) {
        return Map.of("exception", "DuplicateEmailException", "error", e.getMessage());
    }
}
