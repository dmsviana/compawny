package dev.dmsviana.compawny.presentation.controller;

import dev.dmsviana.compawny.business.service.CaregiverService;
import dev.dmsviana.compawny.presentation.controller.contract.CaregiverApiContract;
import dev.dmsviana.compawny.presentation.dto.caregiver.CaregiverResponseDto;
import dev.dmsviana.compawny.presentation.dto.caregiver.CreateCaregiverRequestDto;
import dev.dmsviana.compawny.presentation.dto.caregiver.UpdateCaregiverRequestDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Caregivers")
public class CaregiverController implements CaregiverApiContract {

    private final CaregiverService caregiverService;

    @Override
    public CaregiverResponseDto create(CreateCaregiverRequestDto requestDto) {
        log.info("REST request to create Caregiver");
        return caregiverService.create(requestDto);
    }

    @Override
    public Page<CaregiverResponseDto> getAll(Pageable pageable) {
        log.info("REST request to get all Caregivers");
        return caregiverService.findAll(pageable);
    }

    @Override
    public CaregiverResponseDto getById(Long id) {
        log.info("REST request to get Caregiver : {}", id);
        return caregiverService.findById(id);
    }

    @Override
    public CaregiverResponseDto update(Long id, UpdateCaregiverRequestDto requestDto) {
        log.info("REST request to update Caregiver : {}", id);
        return caregiverService.update(id, requestDto);
    }

    @Override
    public void delete(Long id) {
        log.info("REST request to delete Caregiver : {}", id);
        caregiverService.delete(id);
    }
}