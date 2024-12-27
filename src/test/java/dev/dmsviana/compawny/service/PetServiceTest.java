package dev.dmsviana.compawny.service;

import dev.dmsviana.compawny.business.service.CaregiverService;
import dev.dmsviana.compawny.business.service.PetService;
import dev.dmsviana.compawny.model.entity.Pet;
import dev.dmsviana.compawny.model.entity.types.PetType;
import dev.dmsviana.compawny.model.repository.PetRepository;
import dev.dmsviana.compawny.model.repository.exception.EntityAlreadyExistsException;
import dev.dmsviana.compawny.model.repository.exception.EntityNotFoundException;
import dev.dmsviana.compawny.presentation.dto.pet.CreatePetRequestDto;
import dev.dmsviana.compawny.presentation.dto.pet.PetResponseDto;
import dev.dmsviana.compawny.presentation.dto.pet.mapper.PetMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private PetMapper petMapper;

    @Mock
    private CaregiverService caregiverService;

    @InjectMocks
    private PetService service;

    @Captor
    private ArgumentCaptor<Pet> petCaptor;

    private Pet pet;
    private PetResponseDto responseDto;
    private CreatePetRequestDto createDto;

    @BeforeEach
    void setUp() {
        pet = Pet.builder()
                .id(1L)
                .name("Max")
                .registrationNumber("PET123")
                .type(PetType.DOG)
                .breed("Labrador")
                .birthDate(LocalDate.now().minusYears(2))
                .build();

        createDto = CreatePetRequestDto.builder()
                .name("Max")
                .registrationNumber("PET123")
                .type(PetType.DOG)
                .breed("Labrador")
                .birthDate(LocalDate.now().minusYears(2))
                .build();

        responseDto = PetResponseDto.builder()
                .id(1L)
                .name("Max")
                .registrationNumber("PET123")
                .type(PetType.DOG)
                .build();
    }

    @Nested
    @DisplayName("Create Operations")
    class CreateOperations {

        @Test
        void shouldCreatePetWhenDataIsValid() {
            when(petRepository.existsByRegistrationNumber(anyString())).thenReturn(false);
            when(petMapper.toEntity(any())).thenReturn(pet);
            when(petRepository.save(any())).thenReturn(pet);
            when(petMapper.toDto(any())).thenReturn(responseDto);

            var result = service.create(createDto);

            assertThat(result).isEqualTo(responseDto);
            verify(petRepository).save(petCaptor.capture());
            assertThat(petCaptor.getValue().getRegistrationNumber())
                    .isEqualTo(createDto.getRegistrationNumber());
        }

        @Test
        void shouldThrowExceptionWhenRegistrationExists() {
            when(petRepository.existsByRegistrationNumber(anyString())).thenReturn(true);

            assertThatThrownBy(() -> service.create(createDto))
                    .isInstanceOf(EntityAlreadyExistsException.class)
                    .hasMessage("pet.registration.duplicate");
        }
    }

    @Nested
    @DisplayName("Find Operations")
    class FindOperations {

        @Test
        void shouldFindPetById() {
            when(petRepository.findByIdAndNotDeleted(anyLong()))
                    .thenReturn(Optional.of(pet));
            when(petMapper.toDto(any())).thenReturn(responseDto);

            var result = service.findById(1L);

            assertThat(result).isEqualTo(responseDto);
        }

        @Test
        void shouldThrowExceptionWhenPetNotFound() {
            when(petRepository.findByIdAndNotDeleted(anyLong()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.findById(1L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("pet.notFound");
        }
    }
}