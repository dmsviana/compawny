package dev.dmsviana.compawny.web;

import dev.dmsviana.compawny.model.repository.exception.EntityAlreadyExistsException;
import dev.dmsviana.compawny.model.repository.exception.EntityNotFoundException;
import dev.dmsviana.compawny.presentation.advice.GlobalExceptionHandler;
import dev.dmsviana.compawny.presentation.dto.error.ErrorResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private ServletWebRequest webRequest;

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    private MockHttpServletRequest servletRequest;

    @BeforeEach
    void setUp() {
        servletRequest = new MockHttpServletRequest();
        servletRequest.setRequestURI("/api/v1/test");
        when(webRequest.getRequest()).thenReturn(servletRequest);
    }

    @Test
    @DisplayName("Should return 404 when handling EntityNotFoundException")
    void shouldReturn404WhenHandlingEntityNotFoundException() {
        EntityNotFoundException exception = new EntityNotFoundException("Entity not found");

        ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleEntityNotFound(exception, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Entity not found");
    }

    @Test
    @DisplayName("Should return 409 when handling EntityAlreadyExistsException")
    void shouldReturn409WhenHandlingEntityAlreadyExistsException() {
        EntityAlreadyExistsException exception = new EntityAlreadyExistsException("Entity already exists");

        ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleEntityAlreadyExists(exception, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Entity already exists");
    }

    @Test
    @DisplayName("Should return 409 when handling DataIntegrityViolationException")
    void shouldReturn409WhenHandlingDataIntegrityViolationException() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Data integrity violation");

        ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleDataIntegrityViolation(exception, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Ops! Encontramos um conflito nos dados");
    }

    @Test
    @DisplayName("Should return 400 when handling MethodArgumentNotValidException")
    void shouldReturn400WhenHandlingMethodArgumentNotValidException() {
        MethodArgumentNotValidException exception = createMethodArgumentNotValidException();

        ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleMethodArgumentNotValidException(exception, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Ops! Não conseguimos processar a sua requisição");

        @SuppressWarnings("unchecked")
        var errors = (java.util.Map<String, Object>) response.getBody().getMetadata().get("errors");
        assertThat(errors).isNotNull();
        assertThat(errors).containsKey("field[name]");
    }

    @Test
    @DisplayName("Should return 400 when handling IllegalArgumentException")
    void shouldReturn400WhenHandlingIllegalArgumentException() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleIllegalArgumentException(exception, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid argument");
    }

    @Test
    @DisplayName("Should return 500 when handling generic Exception")
    void shouldReturn500WhenHandlingGenericException() {
        Exception exception = new RuntimeException("Unexpected error");

        ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleGenericException(exception, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Ops! Algo não deu certo");
        assertThat(response.getBody().getMetadata()).containsKey("description");
    }

    private MethodArgumentNotValidException createMethodArgumentNotValidException() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "name", "Name is required");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        return new MethodArgumentNotValidException(
                null,
                bindingResult
        );
    }
}