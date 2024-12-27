package dev.dmsviana.compawny.domain;

import dev.dmsviana.compawny.model.entity.Caregiver;
import dev.dmsviana.compawny.model.entity.Pet;
import dev.dmsviana.compawny.model.entity.types.PetType;
import dev.dmsviana.compawny.model.repository.PetRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
class PetRepositoryTest {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Pet pet;
    private Caregiver caregiver;

    @BeforeEach
    void setUp() {
        caregiver = Caregiver.builder()
                .name("John")
                .cpf("52998224725")
                .email("john@example.com")
                .phone("(11) 99999-9999")
                .hourlyRate(BigDecimal.valueOf(50))
                .build();

        entityManager.persist(caregiver);

        pet = Pet.builder()
                .name("Max")
                .registrationNumber("PET123")
                .type(PetType.DOG)
                .breed("Labrador")
                .birthDate(LocalDate.now().minusYears(2))
                .caregiver(caregiver)
                .build();
    }

    @Nested
    @DisplayName("Save Operations")
    class SaveOperations {

        @Test
        void shouldSavePetSuccessfully() {
            Pet savedPet = petRepository.save(pet);

            assertThat(savedPet).isNotNull();
            assertThat(savedPet.getId()).isNotNull();
            assertThat(savedPet.getName()).isEqualTo(pet.getName());
        }

        @Test
        void shouldThrowExceptionWhenSavingPetWithDuplicateRegistration() {
            entityManager.persistAndFlush(pet);
            entityManager.clear();

            Pet duplicatePet = Pet.builder()
                    .name("Rex")
                    .registrationNumber("PET123")
                    .type(PetType.DOG)
                    .breed("German Shepherd")
                    .birthDate(LocalDate.now().minusYears(1))
                    .build();

            assertThatThrownBy(() -> entityManager.persistAndFlush(duplicatePet))
                    .isInstanceOf(ConstraintViolationException.class);
        }
    }

    @Nested
    @DisplayName("Find Operations")
    class FindOperations {

        @Test
        void shouldFindPetByIdAndNotDeleted() {
            Pet savedPet = petRepository.save(pet);

            Optional<Pet> found = petRepository.findByIdAndNotDeleted(savedPet.getId());

            assertThat(found).isPresent();
            assertThat(found.get().getId()).isEqualTo(savedPet.getId());
        }

        @Test
        void shouldNotFindPetWhenDeleted() {
            pet.setDeleted(true);
            Pet savedPet = petRepository.save(pet);

            Optional<Pet> found = petRepository.findByIdAndNotDeleted(savedPet.getId());

            assertThat(found).isEmpty();
        }
    }

    @AfterEach
    void tearDown() {
        entityManager.clear();
        petRepository.deleteAllInBatch();
    }
}