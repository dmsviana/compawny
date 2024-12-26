package dev.dmsviana.compawny.model.repository.exception;

import dev.dmsviana.compawny.model.exception.CompawnyBusinessException;

import java.util.Map;


public class EntityAlreadyExistsException extends CompawnyBusinessException {

    public EntityAlreadyExistsException() {
        super(
                "Ops! Verificamos que esse cadastro já existe",
                Map.of()
        );
    }

    public EntityAlreadyExistsException(String message) {
        super(
                message,
                Map.of()
        );
    }
}
