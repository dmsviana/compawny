package dev.dmsviana.compawny.presentation.dto.appointment;

import dev.dmsviana.compawny.model.entity.types.AppointmentStatus;
import dev.dmsviana.compawny.presentation.dto.caregiver.CaregiverResponseDto;
import dev.dmsviana.compawny.presentation.dto.pet.PetResponseDto;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AppointmentDtos {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateAppointmentRequestDto {
        @NotNull(message = "{appointment.pet.notnull}")
        private Long petId;

        @NotNull(message = "{appointment.caregiver.notnull}")
        private Long caregiverId;

        @NotNull(message = "{appointment.startTime.notnull}")
        @Future(message = "{appointment.startTime.future}")
        private LocalDateTime startTime;

        @NotNull(message = "{appointment.duration.notnull}")
        @Min(value = 30, message = "{appointment.duration.min}")
        @Max(value = 480, message = "{appointment.duration.max}")
        private Integer durationInMinutes;

        @Size(max = 500, message = "{appointment.notes.size}")
        private String notes;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppointmentResponseDto {
        private Long id;
        private PetResponseDto pet;
        private CaregiverResponseDto caregiver;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Integer durationInMinutes;
        private AppointmentStatus status;
        private BigDecimal totalPrice;
        private String notes;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateAppointmentRequestDto {
        @Future(message = "{appointment.startTime.future}")
        private LocalDateTime startTime;

        @Min(value = 30, message = "{appointment.duration.min}")
        @Max(value = 480, message = "{appointment.duration.max}")
        private Integer durationInMinutes;

        @Size(max = 500, message = "{appointment.notes.size}")
        private String notes;
    }
}