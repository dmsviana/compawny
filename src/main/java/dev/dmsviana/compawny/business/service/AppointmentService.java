package dev.dmsviana.compawny.business.service;

import dev.dmsviana.compawny.model.entity.Appointment;
import dev.dmsviana.compawny.model.entity.Caregiver;
import dev.dmsviana.compawny.model.entity.Pet;
import dev.dmsviana.compawny.model.entity.types.AppointmentStatus;
import dev.dmsviana.compawny.model.repository.AppointmentRepository;
import dev.dmsviana.compawny.model.repository.exception.CaregiverUnavailableException;
import dev.dmsviana.compawny.model.repository.exception.EntityNotFoundException;
import dev.dmsviana.compawny.model.repository.exception.InvalidAppointmentStateException;
import dev.dmsviana.compawny.model.repository.exception.ScheduleConflictException;
import dev.dmsviana.compawny.presentation.dto.appointment.AppointmentDtos.AppointmentResponseDto;
import dev.dmsviana.compawny.presentation.dto.appointment.AppointmentDtos.CreateAppointmentRequestDto;
import dev.dmsviana.compawny.presentation.dto.appointment.AppointmentDtos.UpdateAppointmentRequestDto;
import dev.dmsviana.compawny.presentation.dto.appointment.mapper.AppointmentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PetService petService;
    private final CaregiverService caregiverService;
    private final AppointmentMapper appointmentMapper;

    public AppointmentResponseDto create(CreateAppointmentRequestDto requestDto) {
        log.info("Creating new appointment for pet ID: {} with caregiver ID: {}",
                requestDto.getPetId(), requestDto.getCaregiverId());

        Pet pet = getPet(requestDto.getPetId());
        Caregiver caregiver = getCaregiver(requestDto.getCaregiverId());

        if (!caregiver.getAvailable()) {
            throw new CaregiverUnavailableException(caregiver.getId());
        }

        LocalDateTime endTime = requestDto.getStartTime()
                .plusMinutes(requestDto.getDurationInMinutes());

        if (hasScheduleConflict(caregiver.getId(), requestDto.getStartTime(), endTime)) {
            throw new ScheduleConflictException(caregiver.getId(), requestDto.getStartTime(), endTime);
        }

        Appointment appointment = appointmentMapper.toEntity(requestDto);
        appointment.calculateTotalPrice();

        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment created successfully with ID: {}", savedAppointment.getId());

        return appointmentMapper.toDto(savedAppointment);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponseDto> findAll(Pageable pageable) {
        log.debug("Fetching appointments page: {}", pageable);
        return appointmentRepository.findAll(pageable)
                .map(appointmentMapper::toDto);
    }

    @Transactional(readOnly = true)
    public AppointmentResponseDto findById(Long id) {
        log.debug("Fetching appointment with ID: {}", id);
        return appointmentMapper.toDto(getAppointmentById(id));
    }

    public AppointmentResponseDto update(Long id, UpdateAppointmentRequestDto requestDto) {
        log.info("Updating appointment with ID: {}", id);
        Appointment appointment = getAppointmentById(id);

        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new InvalidAppointmentStateException(appointment.getStatus(), "update");
        }

        if (requestDto.getStartTime() != null || requestDto.getDurationInMinutes() != null) {
            LocalDateTime startTime = requestDto.getStartTime() != null ?
                    requestDto.getStartTime() : appointment.getStartTime();
            Integer duration = requestDto.getDurationInMinutes() != null ?
                    requestDto.getDurationInMinutes() : appointment.getDurationInMinutes();

            LocalDateTime endTime = startTime.plusMinutes(duration);

            if (hasScheduleConflict(appointment.getCaregiver().getId(), startTime, endTime)) {
                throw new ScheduleConflictException(appointment.getCaregiver().getId(), startTime, endTime);
            }
        }

        appointmentMapper.updateEntityFromDto(requestDto, appointment);

        if (requestDto.getStartTime() != null || requestDto.getDurationInMinutes() != null) {
            appointment.calculateTotalPrice();
        }

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment updated successfully");

        return appointmentMapper.toDto(updatedAppointment);
    }

    public void startAppointment(Long id) {
        log.info("Starting appointment with ID: {}", id);
        Appointment appointment = getAppointmentById(id);
        appointment.markAsInProgress();
        appointmentRepository.save(appointment);
        log.info("Appointment marked as in progress");
    }

    public void completeAppointment(Long id) {
        log.info("Completing appointment with ID: {}", id);
        Appointment appointment = getAppointmentById(id);
        appointment.markAsCompleted();
        appointmentRepository.save(appointment);
        log.info("Appointment marked as completed");
    }

    public void cancelAppointment(Long id) {
        log.info("Canceling appointment with ID: {}", id);
        Appointment appointment = getAppointmentById(id);
        appointment.cancel();
        appointmentRepository.save(appointment);
        log.info("Appointment canceled successfully");
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> findUpcomingAppointmentsByCaregiver(Long caregiverId) {
        return appointmentRepository
                .findByCaregiverAndStatusAndStartDateAfter(
                        caregiverId,
                        AppointmentStatus.SCHEDULED,
                        LocalDateTime.now())
                .stream()
                .map(appointmentMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> findUpcomingAppointmentsByPet(Long petId) {
        return appointmentRepository
                .findByPetAndStatusAndStartDateAfter(
                        petId,
                        AppointmentStatus.SCHEDULED,
                        LocalDateTime.now())
                .stream()
                .map(appointmentMapper::toDto)
                .toList();
    }

    private Appointment getAppointmentById(Long id) {
        return appointmentRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> {
                    log.error("Appointment not found with ID: {}", id);
                    return new EntityNotFoundException("appointment.notFound");
                });
    }

    private Pet getPet(Long id) {
        try {
            petService.findById(id);
            return Pet.builder().id(id).build();
        } catch (EntityNotFoundException e) {
            log.error("Pet not found with ID: {}", id);
            throw new EntityNotFoundException("pet.notFound");
        }
    }

    private Caregiver getCaregiver(Long id) {
        try {
            caregiverService.findById(id);
            return Caregiver.builder().id(id).build();
        } catch (EntityNotFoundException e) {
            log.error("Caregiver not found with ID: {}", id);
            throw new EntityNotFoundException("caregiver.notFound");
        }
    }

    private boolean hasScheduleConflict(Long caregiverId, LocalDateTime startTime, LocalDateTime endTime) {
        return appointmentRepository.hasScheduleConflict(caregiverId, startTime, endTime);
    }
}