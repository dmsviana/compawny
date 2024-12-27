package dev.dmsviana.compawny.web;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dmsviana.compawny.business.service.PetService;
import dev.dmsviana.compawny.model.entity.Caregiver;
import dev.dmsviana.compawny.model.entity.Pet;
import dev.dmsviana.compawny.model.entity.types.PetType;
import dev.dmsviana.compawny.model.repository.CaregiverRepository;
import dev.dmsviana.compawny.model.repository.PetRepository;
import dev.dmsviana.compawny.presentation.dto.pet.CreatePetRequestDto;
import dev.dmsviana.compawny.presentation.dto.pet.PetResponseDto;
import dev.dmsviana.compawny.presentation.dto.pet.UpdatePetRequestDto;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PetControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private CaregiverRepository caregiverRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private CreatePetRequestDto createDto;
    private String baseUrl;
    private Caregiver caregiver;

    @PostConstruct
    void postConstruct() {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/v1/pets";

        caregiver = transactionTemplate.execute(status -> {
            Caregiver newCaregiver = Caregiver.builder()
                    .name("John Doe")
                    .cpf("52998224725")
                    .email("john@example.com")
                    .phone("(11) 99999-9999")
                    .description("Pet caregiver")
                    .hourlyRate(BigDecimal.valueOf(50.00))
                    .available(true)
                    .deleted(false)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            return caregiverRepository.saveAndFlush(newCaregiver);
        });

        createDto = CreatePetRequestDto.builder()
                .name("Max")
                .registrationNumber("PET123")
                .type(PetType.DOG)
                .breed("Labrador")
                .birthDate(LocalDate.now().minusYears(2))
                .caregiverId(caregiver.getId())
                .build();
    }

    @AfterEach
    void cleanup() {
        petRepository.deleteAllInBatch();
        caregiverRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("POST /api/v1/pets")
    class CreateTests {

        @Test
        void shouldCreatePet() throws Exception {
            String content = objectMapper.writeValueAsString(createDto);

            mockMvc.perform(post(baseUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content))
                    .andExpect(status().isCreated());
        }

        @Test
        void shouldReturnBadRequestForInvalidData() throws Exception {
            createDto.setName("");

            mockMvc.perform(post(baseUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnConflictForDuplicateRegistrationNumber() throws Exception {
            mockMvc.perform(post(baseUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDto)))
                    .andExpect(status().isCreated());

            mockMvc.perform(post(baseUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDto)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/pets")
    class GetTests {

        @Test
        void shouldReturnPetById() throws Exception {
            String createResponse = mockMvc.perform(post(baseUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDto)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            PetResponseDto createdPet = objectMapper.readValue(createResponse, PetResponseDto.class);

            mockMvc.perform(get(baseUrl + "/{id}", createdPet.getId()))
                    .andExpect(status().isOk());
        }

        @Test
        void shouldReturnNotFoundForInvalidId() throws Exception {
            mockMvc.perform(get(baseUrl + "/{id}", 999L))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/pets")
    class UpdateTests {

        @Test
        void shouldUpdatePet() throws Exception {
            String createResponse = mockMvc.perform(post(baseUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDto)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            PetResponseDto createdPet = objectMapper.readValue(createResponse, PetResponseDto.class);

            UpdatePetRequestDto updateDto = UpdatePetRequestDto.builder()
                    .name("Rex")
                    .breed("German Shepherd")
                    .type(PetType.DOG)
                    .caregiverId(caregiver.getId())
                    .build();

            mockMvc.perform(put(baseUrl + "/{id}", createdPet.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isOk());
        }

        @Test
        void shouldReturnNotFoundWhenUpdatingNonExistentPet() throws Exception {
            UpdatePetRequestDto updateDto = UpdatePetRequestDto.builder()
                    .name("Rex")
                    .breed("German Shepherd")
                    .type(PetType.DOG)
                    .build();

            mockMvc.perform(put(baseUrl + "/{id}", 999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/pets")
    class DeleteTests {

        @Test
        void shouldDeletePet() throws Exception {
            String createResponse = mockMvc.perform(post(baseUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDto)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            PetResponseDto createdPet = objectMapper.readValue(createResponse, PetResponseDto.class);

            mockMvc.perform(delete(baseUrl + "/{id}", createdPet.getId()))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get(baseUrl + "/{id}", createdPet.getId()))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldReturnNotFoundWhenDeletingNonExistentPet() throws Exception {
            mockMvc.perform(delete(baseUrl + "/{id}", 999L))
                    .andExpect(status().isNotFound());
        }
    }
}