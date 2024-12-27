package dev.dmsviana.compawny.model.repository.exception;

import dev.dmsviana.compawny.model.exception.CompawnyBusinessException;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class EntityNotFoundException extends CompawnyBusinessException {

    public EntityNotFoundException() {
        super(
                "Ops! Não foi encontrado nenhum registro correspondente com essa busca",
                HttpStatus.NOT_FOUND,
                Map.of("description", "The requested resource was not found")
        );
    }

    public EntityNotFoundException(String message) {
        super(
                message,
                HttpStatus.NOT_FOUND,
                Map.of("description", "The requested resource was not found")
        );
    }
}