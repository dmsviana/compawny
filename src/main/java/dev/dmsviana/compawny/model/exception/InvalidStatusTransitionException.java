package dev.dmsviana.compawny.model.exception;

import dev.dmsviana.compawny.model.entity.types.AppointmentStatus;

import java.util.Map;

public class InvalidStatusTransitionException extends CompawnyBusinessException {

    public InvalidStatusTransitionException(AppointmentStatus currentStatus, AppointmentStatus newStatus) {
        super(
                String.format("Cannot transition appointment from %s to %s", currentStatus, newStatus),
                Map.of(
                        "currentStatus", currentStatus,
                        "newStatus", newStatus,
                        "message", "Transição de status inválida para o agendamento"
                )
        );
    }
}