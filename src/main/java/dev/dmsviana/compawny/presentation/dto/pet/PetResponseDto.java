package dev.dmsviana.compawny.presentation.dto.pet;


import dev.dmsviana.compawny.model.entity.Pet;
import dev.dmsviana.compawny.model.entity.types.PetType;
import dev.dmsviana.compawny.presentation.dto.caregiver.CaregiverResponseDto;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PetResponseDto {


    private Long id;
    private String name;
    private String registrationNumber;
    private PetType type;
    private String breed;
    private LocalDate birthDate;
    private CaregiverResponseDto caregiver;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
