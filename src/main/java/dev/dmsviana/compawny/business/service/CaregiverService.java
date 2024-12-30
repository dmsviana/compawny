package dev.dmsviana.compawny.business.service;

import dev.dmsviana.compawny.model.entity.Caregiver;
import dev.dmsviana.compawny.model.repository.CaregiverRepository;
import dev.dmsviana.compawny.model.repository.exception.EntityAlreadyExistsException;
import dev.dmsviana.compawny.model.repository.exception.EntityNotFoundException;
import dev.dmsviana.compawny.presentation.dto.caregiver.CaregiverResponseDto;
import dev.dmsviana.compawny.presentation.dto.caregiver.CreateCaregiverRequestDto;
import dev.dmsviana.compawny.presentation.dto.caregiver.UpdateCaregiverRequestDto;
import dev.dmsviana.compawny.presentation.dto.caregiver.mapper.CaregiverMapper;
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
public class CaregiverService {


    private final CaregiverRepository caregiverRepository;
    private final CaregiverMapper caregiverMapper;

    public CaregiverResponseDto create(CreateCaregiverRequestDto requestDto) {
        log.info("Creating new caregiver with CPF: {}", requestDto.getCpf());
        validateUniqueness(requestDto.getCpf(), requestDto.getEmail());

        Caregiver caregiver = caregiverMapper.toEntity(requestDto);
        Caregiver savedCaregiver = caregiverRepository.save(caregiver);

        log.info("Caregiver created successfully with ID: {}", savedCaregiver.getId());
        return caregiverMapper.toDto(savedCaregiver);
    }

    @Transactional(readOnly = true)
    public Page<CaregiverResponseDto> findAll(Pageable pageable) {
        log.debug("Fetching caregivers page: {}", pageable);
        return caregiverRepository.findAll(pageable)
                .map(caregiverMapper::toDto);
    }

    @Transactional(readOnly = true)
    public CaregiverResponseDto findById(Long id) {
        log.debug("Fetching caregiver with ID: {}", id);
        return caregiverMapper.toDto(getCaregiverById(id));
    }

    public CaregiverResponseDto update(Long id, UpdateCaregiverRequestDto requestDto) {
        log.info("Updating caregiver with ID: {}", id);
        Caregiver caregiver = getCaregiverById(id);

        caregiverMapper.updateEntityFromDto(requestDto, caregiver);
        Caregiver updatedCaregiver = caregiverRepository.save(caregiver);

        log.info("Caregiver updated successfully");
        return caregiverMapper.toDto(updatedCaregiver);
    }

    public void delete(Long id) {
        log.info("Deleting caregiver with ID: {}", id);
        Caregiver caregiver = getCaregiverById(id);
        caregiverRepository.delete(caregiver);
        log.info("Caregiver deleted successfully");
    }

    public Caregiver getCaregiverById(Long id) {
        return caregiverRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> {
                    log.error("Caregiver not found with ID: {}", id);
                    return new EntityNotFoundException("caregiver.notFound");
                });
    }

    private void validateUniqueness(String cpf, String email) {
        if (caregiverRepository.existsByCpfAndNotDeleted(cpf)) {
            log.error("CPF already exists: {}", cpf);
            throw new EntityAlreadyExistsException("caregiver.cpf.duplicate");
        }
        if (caregiverRepository.existsByEmailAndNotDeleted(email)) {
            log.error("Email already exists: {}", email);
            throw new EntityAlreadyExistsException("caregiver.email.duplicate");
        }
    }
}