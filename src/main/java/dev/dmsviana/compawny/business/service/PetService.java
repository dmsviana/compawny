package dev.dmsviana.compawny.business.service;

import dev.dmsviana.compawny.model.entity.Caregiver;
import dev.dmsviana.compawny.model.entity.Pet;
import dev.dmsviana.compawny.model.repository.PetRepository;
import dev.dmsviana.compawny.model.repository.exception.EntityAlreadyExistsException;
import dev.dmsviana.compawny.model.repository.exception.EntityNotFoundException;
import dev.dmsviana.compawny.presentation.dto.caregiver.CaregiverResponseDto;
import dev.dmsviana.compawny.presentation.dto.caregiver.mapper.CaregiverMapper;
import dev.dmsviana.compawny.presentation.dto.pet.CreatePetRequestDto;
import dev.dmsviana.compawny.presentation.dto.pet.PetResponseDto;
import dev.dmsviana.compawny.presentation.dto.pet.UpdatePetRequestDto;
import dev.dmsviana.compawny.presentation.dto.pet.mapper.PetMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final PetMapper petMapper;
    private final CaregiverService caregiverService;

    public PetResponseDto create(CreatePetRequestDto requestDto) {
        log.info("Creating new pet with registration: {}", requestDto.getRegistrationNumber());
        validateRegistrationNumber(requestDto.getRegistrationNumber());
        validateCaregiverId(requestDto.getCaregiverId());

        Pet pet = petMapper.toEntity(requestDto);

        if (requestDto.getCaregiverId() != null) {
            var caregiver = caregiverService.getCaregiverById(requestDto.getCaregiverId());
            pet.setCaregiver(caregiver);
        }

        Pet savedPet = petRepository.save(pet);

        log.info("Pet created successfully with ID: {}", savedPet.getId());
        return petMapper.toDto(savedPet);
    }

    @Transactional(readOnly = true)
    public Page<PetResponseDto> findAll(Pageable pageable) {
        log.debug("Fetching pets page: {}", pageable);
        return petRepository.findAll(pageable)
                .map(petMapper::toDto);
    }

    @Transactional(readOnly = true)
    public PetResponseDto findById(Long id) {
        log.debug("Fetching pet with ID: {}", id);
        return petMapper.toDto(getPetById(id));
    }

    public PetResponseDto update(Long id, UpdatePetRequestDto requestDto) {
        log.info("Updating pet with ID: {}", id);
        Pet pet = getPetById(id);
        validateCaregiverId(requestDto.getCaregiverId());

        petMapper.updateEntityFromDto(requestDto, pet);
        Pet updatedPet = petRepository.save(pet);

        log.info("Pet updated successfully");
        return petMapper.toDto(updatedPet);
    }

    public void delete(Long id) {
        log.info("Deleting pet with ID: {}", id);
        Pet pet = getPetById(id);
        petRepository.delete(pet);
        log.info("Pet deleted successfully");
    }

    private Pet getPetById(Long id) {
        return petRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> {
                    log.error("Pet not found with ID: {}", id);
                    return new EntityNotFoundException("pet.notFound");
                });
    }

    private void validateRegistrationNumber(String registrationNumber) {
        if (petRepository.existsByRegistrationNumber(registrationNumber)) {
            log.error("Registration number already exists: {}", registrationNumber);
            throw new EntityAlreadyExistsException("pet.registration.duplicate");
        }
    }

    private void validateCaregiverId(Long caregiverId) {
        if (caregiverId != null) {
            caregiverService.findById(caregiverId);
        }
    }
}