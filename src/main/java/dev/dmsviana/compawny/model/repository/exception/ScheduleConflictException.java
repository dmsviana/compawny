package dev.dmsviana.compawny.model.repository.exception;

import dev.dmsviana.compawny.model.exception.CompawnyBusinessException;
import java.time.LocalDateTime;
import java.util.Map;

public class ScheduleConflictException extends CompawnyBusinessException {

    public ScheduleConflictException(Long caregiverId, LocalDateTime startTime, LocalDateTime endTime) {
        super(
                "Schedule conflict detected for the requested time period",
                Map.of(
                        "caregiverId", caregiverId,
                        "startTime", startTime,
                        "endTime", endTime,
                        "message", "Existe um conflito de horário para o período solicitado"
                )
        );
    }
}