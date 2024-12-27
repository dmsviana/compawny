package dev.dmsviana.compawny.presentation.dto.pet.mapper;

import dev.dmsviana.compawny.model.entity.Caregiver;
import dev.dmsviana.compawny.model.entity.Pet;
import dev.dmsviana.compawny.presentation.dto.caregiver.mapper.CaregiverMapper;
import dev.dmsviana.compawny.presentation.dto.pet.CreatePetRequestDto;
import dev.dmsviana.compawny.presentation.dto.pet.PetResponseDto;
import dev.dmsviana.compawny.presentation.dto.pet.UpdatePetRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CaregiverMapper.class})
public interface PetMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "caregiver", source = "caregiverId")
    Pet toEntity(CreatePetRequestDto dto);

    PetResponseDto toDto(Pet entity);
    List<PetResponseDto> toDtoList(List<Pet> entities);

    void updateEntityFromDto(UpdatePetRequestDto dto, @MappingTarget Pet entity);

    default Caregiver mapCaregiver(Long id) {
        if (id == null) {
            return null;
        }
        return Caregiver.builder()
                .id(id)
                .build();
    }
}