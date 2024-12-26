package dev.dmsviana.compawny.model.repository.exception;

import dev.dmsviana.compawny.model.exception.CompawnyBusinessException;

import java.util.Map;

public class EntityNotFoundException extends CompawnyBusinessException {
    public EntityNotFoundException() {
        super(
                "Ops! Não foi encontrado nenhum registro correspondente com essa busca",
                Map.of()
        );
    }

    public EntityNotFoundException(String message) {
        super(
                message,
                Map.of()
        );
    }
}