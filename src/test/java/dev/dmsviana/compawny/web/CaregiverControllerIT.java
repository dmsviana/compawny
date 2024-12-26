package dev.dmsviana.compawny.web;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dmsviana.compawny.business.service.CaregiverService;
import dev.dmsviana.compawny.model.entity.Caregiver;
import dev.dmsviana.compawny.model.repository.CaregiverRepository;
import dev.dmsviana.compawny.model.repository.exception.EntityNotFoundException;
import dev.dmsviana.compawny.presentation.dto.caregiver.CaregiverResponseDto;
import dev.dmsviana.compawny.presentation.dto.caregiver.CreateCaregiverRequestDto;
import dev.dmsviana.compawny.presentation.dto.caregiver.UpdateCaregiverRequestDto;
import dev.dmsviana.compawny.presentation.dto.error.ErrorResponseDto;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
class CaregiverControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CaregiverRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CaregiverService service;

    private CreateCaregiverRequestDto createDto;
    private String baseUrl;

    @PostConstruct
    void postConstruct() {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/v1/caregivers";
        createDto = CreateCaregiverRequestDto.builder()
                .name("John Doe")
                .cpf("52998224725")
                .email("john@example.com")
                .phone("(11) 99999-9999")
                .description("Pet caregiver")
                .hourlyRate(BigDecimal.valueOf(50.00))
                .build();
    }

    @AfterEach
    void cleanup() {
        repository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("POST /api/v1/caregivers")
    class CreateTests {

        @Test
        void shouldCreateCaregiver() {
            ResponseEntity<CaregiverResponseDto> response = restTemplate.postForEntity(
                    baseUrl,
                    createDto,
                    CaregiverResponseDto.class
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getName()).isEqualTo(createDto.getName());
        }


        @Test
        void shouldReturnBadRequestForInvalidData() throws Exception {
            createDto.setCpf("invalid");

            mockMvc.perform(post(baseUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/caregivers")
    class GetTests {

        @Test
        void shouldReturnCaregiverById() {
            CaregiverResponseDto saved = service.create(createDto);

            ResponseEntity<CaregiverResponseDto> response = restTemplate.getForEntity(
                    baseUrl + "/{id}",
                    CaregiverResponseDto.class,
                    saved.getId()
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getId()).isEqualTo(saved.getId());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/caregivers")
    class UpdateTests {

        @Test
        void shouldUpdateCaregiver() {
            CaregiverResponseDto saved = service.create(createDto);
            UpdateCaregiverRequestDto updateDto = UpdateCaregiverRequestDto.builder()
                    .phone("(11) 88888-8888")
                    .hourlyRate(BigDecimal.valueOf(60.00))
                    .build();

            HttpEntity<UpdateCaregiverRequestDto> request = new HttpEntity<>(updateDto);
            ResponseEntity<CaregiverResponseDto> putResponse = restTemplate.exchange(
                    baseUrl + "/{id}",
                    HttpMethod.PUT,
                    request,
                    CaregiverResponseDto.class,
                    saved.getId()
            );

            assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(putResponse.getBody().getPhone()).isEqualTo(updateDto.getPhone());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/caregivers")
    class DeleteTests {

        @Test
        void shouldDeleteCaregiver() throws Exception {
            CaregiverResponseDto saved = service.create(createDto);

            mockMvc.perform(delete(baseUrl + "/{id}", saved.getId()))
                    .andExpect(status().isNoContent());

        }
    }
}