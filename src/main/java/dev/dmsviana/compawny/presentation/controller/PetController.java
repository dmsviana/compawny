package dev.dmsviana.compawny.presentation.controller;

import dev.dmsviana.compawny.business.service.PetService;
import dev.dmsviana.compawny.presentation.controller.contract.PetApiContract;
import dev.dmsviana.compawny.presentation.dto.pet.CreatePetRequestDto;
import dev.dmsviana.compawny.presentation.dto.pet.PetResponseDto;
import dev.dmsviana.compawny.presentation.dto.pet.UpdatePetRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PetController implements PetApiContract {

    private final PetService petService;

    @Override
    public PetResponseDto create(CreatePetRequestDto request) {
        log.info("Creating pet with registration: {}", request.getRegistrationNumber());
        return petService.create(request);
    }

    @Override
    public PetResponseDto getById(Long id) {
        log.info("Finding pet with id: {}", id);
        return petService.findById(id);
    }

    @Override
    public Page<PetResponseDto> getAll(Pageable pageable) {
        log.info("Listing all pets");
        return petService.findAll(pageable);
    }

    @Override
    public PetResponseDto update(Long id, UpdatePetRequestDto request) {
        log.info("REST request to update pet : {}", id);
        return petService.update(id, request);
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting pet with id: {}", id);
        petService.delete(id);
    }
}