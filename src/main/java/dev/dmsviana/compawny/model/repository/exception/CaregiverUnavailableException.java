package dev.dmsviana.compawny.model.repository.exception;

import dev.dmsviana.compawny.model.exception.CompawnyBusinessException;
import java.util.Map;

public class CaregiverUnavailableException extends CompawnyBusinessException {

    public CaregiverUnavailableException(Long caregiverId) {
        super(
                "Caregiver is not available for appointments",
                Map.of(
                        "caregiverId", caregiverId,
                        "message", "O cuidador não está disponível para agendamentos no momento"
                )
        );
    }
}