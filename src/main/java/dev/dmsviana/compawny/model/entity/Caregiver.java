package dev.dmsviana.compawny.model.entity;

import jakarta.persistence.*;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import static java.math.RoundingMode.HALF_UP;

@Entity
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(of = {"id", "cpf", "email"})
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "caregivers",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_caregiver_cpf", columnNames = "cpf"),
                @UniqueConstraint(name = "uk_caregiver_email", columnNames = "email")
        },
        indexes = {
                @Index(name = "idx_caregiver_cpf", columnList = "cpf"),
                @Index(name = "idx_caregiver_email", columnList = "email"),
                @Index(name = "idx_caregiver_available", columnList = "available")
        }
)
@SQLDelete(sql = "UPDATE caregivers SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Caregiver implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{caregiver.name.notblank}")
    @Size(min = 3, max = 100, message = "{caregiver.name.size}")
    @Column(nullable = false, length = 100)
    private String name;

    @CPF(message = "{caregiver.cpf.invalid}")
    @NotBlank(message = "{caregiver.cpf.notblank}")
    @Column(nullable = false, length = 11)
    private String cpf;

    @Email(message = "{caregiver.email.invalid}")
    @NotBlank(message = "{caregiver.email.notblank}")
    @Column(nullable = false, length = 100)
    private String email;

    @NotBlank(message = "{caregiver.phone.notblank}")
    @Pattern(
            regexp = "^\\(\\d{2}\\)\\s\\d{5}-\\d{4}$",
            message = "{caregiver.phone.pattern}"
    )
    @Column(nullable = false, length = 15)
    private String phone;

    @Size(max = 500, message = "{caregiver.description.size}")
    @Column(length = 500)
    private String description;

    @NotNull(message = "{caregiver.hourlyRate.notnull}")
    @DecimalMin(value = "0.0", message = "{caregiver.hourlyRate.min}")
    @Digits(integer = 8, fraction = 2, message = "{caregiver.hourlyRate.digits}")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @Builder.Default
    @Column(nullable = false)
    private Boolean available = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean deleted = false;

    @Version
    private Long version;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = Objects.requireNonNull(hourlyRate, "hourlyRate must not be null")
                .setScale(2, HALF_UP);
    }

    public void markAsUnavailable() {
        this.available = false;
    }

    public void markAsAvailable() {
        this.available = true;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}