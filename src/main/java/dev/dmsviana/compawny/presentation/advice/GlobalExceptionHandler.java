package dev.dmsviana.compawny.presentation.advice;

import dev.dmsviana.compawny.model.exception.CompawnyBusinessException;
import dev.dmsviana.compawny.model.repository.exception.EntityAlreadyExistsException;
import dev.dmsviana.compawny.model.repository.exception.EntityNotFoundException;
import dev.dmsviana.compawny.presentation.dto.error.ErrorResponseDto;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityNotFound(
            EntityNotFoundException ex,
            ServletWebRequest request) {
        var error = createError(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                ex.getMetadata(),
                request
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityAlreadyExists(
            EntityAlreadyExistsException ex,
            ServletWebRequest request) {
        var error = createError(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                ex.getMetadata(),
                request
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolation(
            DataIntegrityViolationException e,
            ServletWebRequest request) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("description", e.getMostSpecificCause().getMessage());

        var error = createError(
                HttpStatus.CONFLICT,
                "Ops! Encontramos um conflito nos dados",
                metadata,
                request
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(CompawnyBusinessException.class)
    public ResponseEntity<ErrorResponseDto> handleBusinessException(
            CompawnyBusinessException ex,
            ServletWebRequest request) {
        var error = createError(
                ex.getStatus(),
                ex.getMessage(),
                ex.getMetadata(),
                request
        );
        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(
            IllegalArgumentException e,
            ServletWebRequest request) {
        var error = createError(
                HttpStatus.BAD_REQUEST,
                e.getMessage(),
                Map.of(),
                request
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e,
            ServletWebRequest request) {
        Map<String, Object> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(fieldErr -> {
            errors.put(
                    "field[" + fieldErr.getField() + "]",
                    fieldErr.getDefaultMessage()
            );
        });

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("errors", errors);

        var error = createError(
                HttpStatus.BAD_REQUEST,
                "Ops! Não conseguimos processar a sua requisição",
                metadata,
                request
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(
            Exception e,
            ServletWebRequest request) {
        var error = createError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ops! Algo não deu certo",
                Map.of(
                        "description", e.getMessage()
                ),
                request
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private ErrorResponseDto createError(
            HttpStatus status,
            String message,
            Map<String, Object> metadata,
            ServletWebRequest request
    ) {
        return new ErrorResponseDto(
                status.value(),
                status,
                message,
                metadata,
                request.getRequest().getRequestURL().toString(),
                LocalDateTime.now()
        );
    }
}