package dev.dmsviana.compawny.model.entity;

import dev.dmsviana.compawny.model.entity.types.AppointmentStatus;
import dev.dmsviana.compawny.model.exception.CompawnyBusinessException;
import dev.dmsviana.compawny.model.exception.InvalidStatusTransitionException;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@ToString
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "appointments",
        indexes = {
                @Index(name = "idx_appointment_pet", columnList = "pet_id"),
                @Index(name = "idx_appointment_caregiver", columnList = "caregiver_id"),
                @Index(name = "idx_appointment_status", columnList = "status"),
                @Index(name = "idx_appointment_start_time", columnList = "start_time"),
        }
)
public class Appointment implements Serializable {


        @Serial
        private static final long serialVersionUID = 1L;

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "pet_id", nullable = false)
        @NotNull(message = "{appointment.pet.notnull}")
        private Pet pet;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "caregiver_id", nullable = false)
        @NotNull(message = "{appointment.caregiver.notnull}")
        private Caregiver caregiver;

        @Column(nullable = false)
        @NotNull(message = "{appointment.startTime.notnull}")
        @Future(message = "{appointment.startTime.future}")
        private LocalDateTime startTime;

        @Column(nullable = false)
        @NotNull(message = "{appointment.duration.notnull}")
        @Min(value = 1, message = "{appointment.duration.min}")
        private Integer durationInMinutes;

        @Column(nullable = false)
        @Enumerated(EnumType.STRING)
        private AppointmentStatus status;

        @Column(nullable = false, precision = 10, scale = 2)
        @NotNull(message = "{appointment.totalPrice.notnull}")
        @DecimalMin(value = "0.0", message = "{appointment.totalPrice.min}")
        private BigDecimal totalPrice;

        @Size(max = 500, message = "{appointment.notes.size}")
        @Column(length = 500)
        private String notes;

        @CreatedDate
        @Column(nullable = false, updatable = false)
        private LocalDateTime createdAt;

        @LastModifiedDate
        private LocalDateTime updatedAt;

        @Builder.Default
        @Column(nullable = false)
        private Boolean deleted = false;

        @PrePersist
        protected void onCreate() {
                status = AppointmentStatus.SCHEDULED;
                createdAt = LocalDateTime.now();
                updatedAt = LocalDateTime.now();
        }

        @PreUpdate
        protected void onUpdate() {
                updatedAt = LocalDateTime.now();
        }

        public LocalDateTime getEndTime() {
                return startTime.plusMinutes(durationInMinutes);
        }

        public void calculateTotalPrice() {
                BigDecimal hourlyRate = this.caregiver.getHourlyRate();
                BigDecimal hours = BigDecimal.valueOf(durationInMinutes)
                        .divide(BigDecimal.valueOf(60), 2, java.math.RoundingMode.HALF_UP);
                this.totalPrice = hourlyRate.multiply(hours);
        }

        public void markAsInProgress() {
                validateStatusTransition(AppointmentStatus.IN_PROGRESS);
                this.status = AppointmentStatus.IN_PROGRESS;
        }

        public void markAsCompleted() {
                validateStatusTransition(AppointmentStatus.COMPLETED);
                this.status = AppointmentStatus.COMPLETED;
        }

        public void cancel() {
                validateStatusTransition(AppointmentStatus.CANCELLED);
                this.status = AppointmentStatus.CANCELLED;
        }

        private void validateStatusTransition(AppointmentStatus newStatus) {
                if (!status.canTransitionTo(newStatus)) {
                        throw new InvalidStatusTransitionException(status, newStatus) {
                        };
                }
        }


}
