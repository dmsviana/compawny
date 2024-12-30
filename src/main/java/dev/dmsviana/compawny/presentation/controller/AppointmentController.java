package dev.dmsviana.compawny.presentation.controller;

import dev.dmsviana.compawny.business.service.AppointmentService;
import dev.dmsviana.compawny.presentation.controller.contract.AppointmentApiContract;
import dev.dmsviana.compawny.presentation.dto.appointment.AppointmentDtos.AppointmentResponseDto;
import dev.dmsviana.compawny.presentation.dto.appointment.AppointmentDtos.CreateAppointmentRequestDto;
import dev.dmsviana.compawny.presentation.dto.appointment.AppointmentDtos.UpdateAppointmentRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AppointmentController implements AppointmentApiContract {

    private final AppointmentService appointmentService;

    @Override
    public AppointmentResponseDto create(CreateAppointmentRequestDto request) {
        log.info("REST request to create Appointment for pet ID: {} with caregiver ID: {}",
                request.getPetId(), request.getCaregiverId());
        return appointmentService.create(request);
    }

    @Override
    public Page<AppointmentResponseDto> getAll(Pageable pageable) {
        log.info("REST request to get all Appointments");
        return appointmentService.findAll(pageable);
    }

    @Override
    public AppointmentResponseDto getById(Long id) {
        log.info("REST request to get Appointment : {}", id);
        return appointmentService.findById(id);
    }

    @Override
    public AppointmentResponseDto update(Long id, UpdateAppointmentRequestDto request) {
        log.info("REST request to update Appointment : {}", id);
        return appointmentService.update(id, request);
    }

    @Override
    public AppointmentResponseDto startAppointment(Long id) {
        log.info("REST request to start Appointment : {}", id);
        appointmentService.startAppointment(id);
        return appointmentService.findById(id);
    }

    @Override
    public AppointmentResponseDto completeAppointment(Long id) {
        log.info("REST request to complete Appointment : {}", id);
        appointmentService.completeAppointment(id);
        return appointmentService.findById(id);
    }

    @Override
    public AppointmentResponseDto cancelAppointment(Long id) {
        log.info("REST request to cancel Appointment : {}", id);
        appointmentService.cancelAppointment(id);
        return appointmentService.findById(id);
    }

    @Override
    public List<AppointmentResponseDto> getUpcomingAppointmentsByCaregiver(Long caregiverId) {
        log.info("REST request to get upcoming Appointments for caregiver : {}", caregiverId);
        return appointmentService.findUpcomingAppointmentsByCaregiver(caregiverId);
    }

    @Override
    public List<AppointmentResponseDto> getUpcomingAppointmentsByPet(Long petId) {
        log.info("REST request to get upcoming Appointments for pet : {}", petId);
        return appointmentService.findUpcomingAppointmentsByPet(petId);
    }
}