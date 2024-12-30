package dev.dmsviana.compawny.presentation.dto.caregiver.mapper;

import dev.dmsviana.compawny.model.entity.Caregiver;
import dev.dmsviana.compawny.presentation.dto.caregiver.CaregiverResponseDto;
import dev.dmsviana.compawny.presentation.dto.caregiver.CreateCaregiverRequestDto;
import dev.dmsviana.compawny.presentation.dto.caregiver.UpdateCaregiverRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CaregiverMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Caregiver toEntity(CreateCaregiverRequestDto dto);

    CaregiverResponseDto toDto(Caregiver entity);
    List<CaregiverResponseDto> toDtoList(List<Caregiver> entities);

    void updateEntityFromDto(UpdateCaregiverRequestDto dto, @MappingTarget Caregiver entity);
}