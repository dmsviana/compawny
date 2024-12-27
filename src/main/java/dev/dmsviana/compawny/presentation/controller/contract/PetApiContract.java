package dev.dmsviana.compawny.presentation.controller.contract;

import dev.dmsviana.compawny.presentation.dto.error.ErrorResponseDto;
import dev.dmsviana.compawny.presentation.dto.pet.CreatePetRequestDto;
import dev.dmsviana.compawny.presentation.dto.pet.PetResponseDto;
import dev.dmsviana.compawny.presentation.dto.pet.UpdatePetRequestDto;
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

@Tag(name = "Pets", description = "API for managing pets")
@RequestMapping("/api/v1/pets")
public interface PetApiContract {

    @Operation(summary = "Create a new pet")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Pet created successfully",
                    content = @Content(schema = @Schema(implementation = PetResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Registration number already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping
    @ResponseStatus(CREATED)
    PetResponseDto create(@Valid @RequestBody CreatePetRequestDto request);

    @Operation(summary = "Get all pets")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of pets retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PetResponseDto.class),
                            array = @ArraySchema(schema = @Schema(implementation = PetResponseDto.class))
                    )
            )
    })
    @GetMapping
    @ResponseStatus(OK)
    Page<PetResponseDto> getAll(@ParameterObject @PageableDefault(sort = "name") Pageable pageable);

    @Operation(summary = "Get pet by ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pet found",
                    content = @Content(schema = @Schema(implementation = PetResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pet not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/{id}")
    @ResponseStatus(OK)
    PetResponseDto getById(@PathVariable Long id);

    @Operation(summary = "Delete pet")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Pet deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pet not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    void delete(@PathVariable Long id);

    @Operation(summary = "Update pet")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pet updated successfully",
                    content = @Content(schema = @Schema(implementation = PetResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pet not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PutMapping("/{id}")
    @ResponseStatus(OK)
    PetResponseDto update(@PathVariable Long id, @Valid @RequestBody UpdatePetRequestDto request);
}