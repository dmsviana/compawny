package dev.dmsviana.compawny.service;

import dev.dmsviana.compawny.business.service.CaregiverService;
import dev.dmsviana.compawny.model.entity.Caregiver;
import dev.dmsviana.compawny.model.repository.CaregiverRepository;
import dev.dmsviana.compawny.model.repository.exception.EntityAlreadyExistsException;
import dev.dmsviana.compawny.model.repository.exception.EntityNotFoundException;
import dev.dmsviana.compawny.presentation.dto.caregiver.CaregiverResponseDto;
import dev.dmsviana.compawny.presentation.dto.caregiver.CreateCaregiverRequestDto;
import dev.dmsviana.compawny.presentation.dto.caregiver.UpdateCaregiverRequestDto;
import dev.dmsviana.compawny.presentation.dto.caregiver.mapper.CaregiverMapper;
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

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CaregiverServiceTest {

    @Mock
    private CaregiverRepository repository;

    @Mock
    private CaregiverMapper mapper;

    @InjectMocks
    private CaregiverService service;

    @Captor
    private ArgumentCaptor<Caregiver> caregiverCaptor;

    private Caregiver caregiver;
    private CaregiverResponseDto responseDto;
    private CreateCaregiverRequestDto createDto;

    @BeforeEach
    void setUp() {
        caregiver = Caregiver.builder()
                .id(1L)
                .name("John Doe")
                .cpf("52998224725")
                .email("john@example.com")
                .phone("(11) 99999-9999")
                .hourlyRate(BigDecimal.valueOf(50.00))
                .build();

        responseDto = CaregiverResponseDto.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        createDto = CreateCaregiverRequestDto.builder()
                .name("John Doe")
                .cpf("52998224725")
                .email("john@example.com")
                .build();
    }

    @Nested
    @DisplayName("Create Operations")
    class CreateOperations {

        @Test
        @DisplayName("Should create caregiver when data is valid")
        void shouldCreateCaregiverWhenDataIsValid() {
            when(repository.existsByCpfAndNotDeleted(anyString())).thenReturn(false);
            when(repository.existsByEmailAndNotDeleted(anyString())).thenReturn(false);
            when(mapper.toEntity(any())).thenReturn(caregiver);
            when(repository.save(any())).thenReturn(caregiver);
            when(mapper.toDto(any())).thenReturn(responseDto);

            CaregiverResponseDto result = service.create(createDto);

            assertThat(result).isNotNull()
                    .isEqualTo(responseDto);

            verify(repository).save(caregiverCaptor.capture());
            assertThat(caregiverCaptor.getValue().getCpf())
                    .isEqualTo(createDto.getCpf());
        }

        @Test
        @DisplayName("Should throw exception when CPF already exists")
        void shouldThrowExceptionWhenCpfAlreadyExists() {
            when(repository.existsByCpfAndNotDeleted(anyString())).thenReturn(true);

            assertThatThrownBy(() -> service.create(createDto))
                    .isInstanceOf(EntityAlreadyExistsException.class)
                    .hasMessage("caregiver.cpf.duplicate");
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void shouldThrowExceptionWhenEmailAlreadyExists() {
            when(repository.existsByCpfAndNotDeleted(anyString())).thenReturn(false);
            when(repository.existsByEmailAndNotDeleted(anyString())).thenReturn(true);

            assertThatThrownBy(() -> service.create(createDto))
                    .isInstanceOf(EntityAlreadyExistsException.class)
                    .hasMessage("caregiver.email.duplicate");
        }
    }

    @Nested
    @DisplayName("Find Operations")
    class FindOperations {

        @Test
        @DisplayName("Should find caregiver by ID")
        void shouldFindCaregiverById() {
            when(repository.findByIdAndNotDeleted(anyLong()))
                    .thenReturn(Optional.of(caregiver));
            when(mapper.toDto(any(Caregiver.class))).thenReturn(responseDto);

            CaregiverResponseDto result = service.findById(1L);

            assertThat(result).isNotNull().isEqualTo(responseDto);
        }

        @Test
        @DisplayName("Should throw exception when caregiver not found")
        void shouldThrowExceptionWhenCaregiverNotFound() {
            when(repository.findByIdAndNotDeleted(anyLong()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.findById(1L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("caregiver.notFound");
        }
    }

    @Nested
    @DisplayName("Update Operations")
    class UpdateOperations {

        @Test
        @DisplayName("Should update caregiver successfully")
        void shouldUpdateCaregiverSuccessfully() {
            UpdateCaregiverRequestDto updateDto = new UpdateCaregiverRequestDto();
            updateDto.setPhone("(11) 88888-8888");

            when(repository.findByIdAndNotDeleted(anyLong()))
                    .thenReturn(Optional.of(caregiver));
            when(repository.save(any())).thenReturn(caregiver);
            when(mapper.toDto(any())).thenReturn(responseDto);

            CaregiverResponseDto result = service.update(1L, updateDto);

            assertThat(result).isNotNull();
            verify(mapper).updateEntityFromDto(eq(updateDto), any(Caregiver.class));
            verify(repository).save(caregiverCaptor.capture());
        }
    }

    @Nested
    @DisplayName("Delete Operations")
    class DeleteOperations {

        @Test
        @DisplayName("Should delete caregiver successfully")
        void shouldDeleteCaregiverSuccessfully() {
            when(repository.findByIdAndNotDeleted(anyLong()))
                    .thenReturn(Optional.of(caregiver));

            service.delete(1L);

            verify(repository).delete(caregiverCaptor.capture());
            assertThat(caregiverCaptor.getValue()).isEqualTo(caregiver);
        }
    }
}
