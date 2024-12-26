package dev.dmsviana.compawny.presentation.controller.contract;

import dev.dmsviana.compawny.presentation.dto.caregiver.CaregiverResponseDto;
import dev.dmsviana.compawny.presentation.dto.caregiver.CreateCaregiverRequestDto;
import dev.dmsviana.compawny.presentation.dto.caregiver.UpdateCaregiverRequestDto;
import dev.dmsviana.compawny.presentation.dto.error.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@Tag(name = "Caregivers", description = "API for managing caregivers")
@RequestMapping("/api/v1/caregivers")
public interface CaregiverApiContract {

    @Operation(summary = "Create a new caregiver")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Caregiver created successfully",
                    content = @Content(schema = @Schema(implementation = CaregiverResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "CPF or email already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping
    @ResponseStatus(CREATED)
    CaregiverResponseDto create(@RequestBody @Valid CreateCaregiverRequestDto requestDto);

    @Operation(summary = "Get all caregivers")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of caregivers retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CaregiverResponseDto.class),
                            array = @ArraySchema(schema = @Schema(implementation = CaregiverResponseDto.class))
                    )
            )
    })
    @GetMapping
    @ResponseStatus(OK)
    Page<CaregiverResponseDto> getAll(
            @ParameterObject @PageableDefault(sort = "name") Pageable pageable
    );

    @Operation(summary = "Get caregiver by ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Caregiver found",
                    content = @Content(schema = @Schema(implementation = CaregiverResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Caregiver not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/{id}")
    @ResponseStatus(OK)
    CaregiverResponseDto getById(@PathVariable Long id);

    @Operation(summary = "Update caregiver")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Caregiver updated successfully",
                    content = @Content(schema = @Schema(implementation = CaregiverResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Caregiver not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PutMapping("/{id}")
    @ResponseStatus(OK)
    CaregiverResponseDto update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateCaregiverRequestDto requestDto
    );

    @Operation(summary = "Delete caregiver")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Caregiver deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Caregiver not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    void delete(@PathVariable Long id);
}