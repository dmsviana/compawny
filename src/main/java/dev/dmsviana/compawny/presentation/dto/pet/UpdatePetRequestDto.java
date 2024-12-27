package dev.dmsviana.compawny.presentation.dto.pet;

import dev.dmsviana.compawny.model.entity.types.PetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePetRequestDto {

    @NotBlank(message = "{pet.name.notblank}")
    @Size(min = 2, max = 100)
    private String name;

    @NotNull(message = "{pet.breed.notnull}")
    private String breed;

    @NotNull(message = "{pet.type.notnull}")
    private PetType type;

    private Long caregiverId;
}