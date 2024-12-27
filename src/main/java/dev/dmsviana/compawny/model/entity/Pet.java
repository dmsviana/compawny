package dev.dmsviana.compawny.model.entity;

import dev.dmsviana.compawny.model.entity.types.PetType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(of = {"id", "name", "registrationNumber"})
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "pets", uniqueConstraints = {@UniqueConstraint(name = "uk_pet_registration_number", columnNames = "registration_number")}, indexes = {@Index(name = "idx_pet_registration", columnList = "registration_number"), @Index(name = "idx_pet_caregiver", columnList = "caregiver_id")})
public class Pet implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{pet.name.notblank}")
    @Size(min = 2, max = 100, message = "{pet.name.size}")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "{pet.registration.notblank}")
    @Column(name = "registration_number", nullable = false, unique = true)
    private String registrationNumber;

    @NotNull(message = "{pet.type.notnull}")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PetType type;

    @NotNull(message = "{pet.breed.notnull}")
    @Column(nullable = false, length = 50)
    private String breed;

    @NotNull(message = "{pet.birthDate.notnull}")
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caregiver_id")
    private Caregiver caregiver;

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