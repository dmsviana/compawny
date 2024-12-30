package dev.dmsviana.compawny.presentation.dto.appointment.mapper;

import dev.dmsviana.compawny.model.entity.Appointment;
import dev.dmsviana.compawny.model.entity.Pet;
import dev.dmsviana.compawny.presentation.dto.appointment.AppointmentDtos.AppointmentResponseDto;
import dev.dmsviana.compawny.presentation.dto.appointment.AppointmentDtos.CreateAppointmentRequestDto;
import dev.dmsviana.compawny.presentation.dto.appointment.AppointmentDtos.UpdateAppointmentRequestDto;
import dev.dmsviana.compawny.presentation.dto.caregiver.mapper.CaregiverMapper;
import dev.dmsviana.compawny.presentation.dto.pet.mapper.PetMapper;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {PetMapper.class, CaregiverMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AppointmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "pet", source = "petId")
    @Mapping(target = "caregiver", source = "caregiverId")
    Appointment toEntity(CreateAppointmentRequestDto dto);

    @Mapping(target = "endTime", expression = "java(entity.getEndTime())")
    AppointmentResponseDto toDto(Appointment entity);

    List<AppointmentResponseDto> toDtoList(List<Appointment> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pet", ignore = true)
    @Mapping(target = "caregiver", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntityFromDto(UpdateAppointmentRequestDto dto, @MappingTarget Appointment entity);

    default Pet mapPet(Long id) {
        if (id == null) {
            return null;
        }
        return Pet.builder()
                .id(id)
                .build();
    }
}