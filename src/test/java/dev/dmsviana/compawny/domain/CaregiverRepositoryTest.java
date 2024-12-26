package dev.dmsviana.compawny.domain;

import dev.dmsviana.compawny.model.entity.Caregiver;
import dev.dmsviana.compawny.model.repository.CaregiverRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
class CaregiverRepositoryTest {

    @Autowired
    private CaregiverRepository caregiverRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Caregiver caregiver;

    @BeforeEach
    void setUp() {
        caregiver = Caregiver.builder()
                .name("John Doe")
                .cpf("52998224725")
                .email("john@example.com")
                .phone("(11) 99999-9999")
                .description("Experienced pet caregiver")
                .hourlyRate(BigDecimal.valueOf(50.00))
                .build();
    }

    @Nested
    @DisplayName("Save Operations")
    class SaveOperations {

        @Test
        @DisplayName("Should save caregiver successfully")
        void shouldSaveCaregiverSuccessfully() {
            Caregiver savedCaregiver = caregiverRepository.save(caregiver);

            assertThat(savedCaregiver).isNotNull();
            assertThat(savedCaregiver.getId()).isNotNull();
            assertThat(savedCaregiver.getName()).isEqualTo(caregiver.getName());
        }

        @Test
        @DisplayName("Should throw exception when saving caregiver with invalid CPF")
        void shouldThrowExceptionWhenSavingCaregiverWithInvalidCPF() {
            caregiver.setCpf("invalid");
            assertThatThrownBy(() -> entityManager.persistAndFlush(caregiver))
                    .isInstanceOf(ConstraintViolationException.class);
        }

        @Test
        @DisplayName("Should throw exception when saving caregiver with duplicate email")
        void shouldThrowExceptionWhenSavingCaregiverWithDuplicateEmail() {
            entityManager.persistAndFlush(caregiver);
            entityManager.clear();

            var duplicateCaregiver = Caregiver.builder()
                    .name("Jane Doe")
                    .cpf("77855326190")
                    .email("john@example.com")
                    .phone("(11) 88888-8888")
                    .hourlyRate(BigDecimal.valueOf(45.00))
                    .build();

            assertThatThrownBy(() -> entityManager.persistAndFlush(duplicateCaregiver))
                    .isInstanceOf(ConstraintViolationException.class);
        }
    }

    @Nested
    @DisplayName("Find Operations")
    class FindOperations {

        @Test
        @DisplayName("Should find caregiver by ID and not deleted")
        void shouldFindCaregiverByIdAndNotDeleted() {
            Caregiver savedCaregiver = caregiverRepository.save(caregiver);

            Optional<Caregiver> foundCaregiver = caregiverRepository.findByIdAndNotDeleted(savedCaregiver.getId());

            assertThat(foundCaregiver).isPresent();
            assertThat(foundCaregiver.get().getId()).isEqualTo(savedCaregiver.getId());
        }

        @Test
        @DisplayName("Should not find caregiver when deleted")
        void shouldNotFindCaregiverWhenDeleted() {
            caregiver.setDeleted(true);
            Caregiver savedCaregiver = caregiverRepository.save(caregiver);

            Optional<Caregiver> foundCaregiver = caregiverRepository.findByIdAndNotDeleted(savedCaregiver.getId());

            assertThat(foundCaregiver).isEmpty();
        }
    }

    @Nested
    @DisplayName("Exists Operations")
    class ExistsOperations {

        @Test
        @DisplayName("Should return true when CPF exists and not deleted")
        void shouldReturnTrueWhenCpfExistsAndNotDeleted() {
            caregiverRepository.save(caregiver);

            boolean exists = caregiverRepository.existsByCpfAndNotDeleted(caregiver.getCpf());

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("Should return false when CPF exists but is deleted")
        void shouldReturnFalseWhenCpfExistsButIsDeleted() {
            caregiver.setDeleted(true);
            caregiverRepository.save(caregiver);

            boolean exists = caregiverRepository.existsByCpfAndNotDeleted(caregiver.getCpf());

            assertThat(exists).isFalse();
        }
    }

    @AfterEach
    void tearDown() {
        entityManager.clear();
        caregiverRepository.deleteAllInBatch();
    }
}