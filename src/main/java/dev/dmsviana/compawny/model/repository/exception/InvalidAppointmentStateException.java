package dev.dmsviana.compawny.model.repository.exception;

import dev.dmsviana.compawny.model.entity.types.AppointmentStatus;
import dev.dmsviana.compawny.model.exception.CompawnyBusinessException;
import java.util.Map;

public class InvalidAppointmentStateException extends CompawnyBusinessException {

    public InvalidAppointmentStateException(AppointmentStatus currentStatus, String operation) {
        super(
                String.format("Cannot perform %s operation on appointment with status %s", operation, currentStatus),
                Map.of(
                        "currentStatus", currentStatus,
                        "operation", operation,
                        "message", "Não é possível realizar esta operação no estado atual do agendamento"
                )
        );
    }
}