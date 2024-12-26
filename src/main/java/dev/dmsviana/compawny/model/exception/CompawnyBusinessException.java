package dev.dmsviana.compawny.model.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public abstract class CompawnyBusinessException extends CompawnyException {

    protected CompawnyBusinessException(
            String message,
            HttpStatus status,
            Map<String, Object> metadata
    ) {
        super(message, status, metadata);
    }

    protected CompawnyBusinessException(
            String message,
            Map<String, Object> metadata
    ) {
        super(message, HttpStatus.BAD_REQUEST, metadata);
    }

    protected CompawnyBusinessException(
            String message
    ) {
        super(
                message,
                HttpStatus.BAD_REQUEST,
                Map.of("description", "Ops! Não conseguimos processar a sua requisição")
        );
    }
}