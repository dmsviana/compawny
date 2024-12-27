package dev.dmsviana.compawny.presentation.dto.pet;


import dev.dmsviana.compawny.model.entity.Pet;
import dev.dmsviana.compawny.model.entity.types.PetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatePetRequestDto {


    @NotBlank(message = "{pet.name.notblank}")
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank(message = "{pet.registration.notblank}")
    private String registrationNumber;

    @NotNull(message = "{pet.type.notnull}")
    private PetType type;

    @NotNull(message = "{pet.breed.notnull}")
    private String breed;

    @NotNull(message = "{pet.birthDate.notnull}")
    private LocalDate birthDate;

    private Long caregiverId;

}
