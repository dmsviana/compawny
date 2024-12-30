package dev.dmsviana.compawny.presentation.controller.contract;

import dev.dmsviana.compawny.presentation.dto.appointment.AppointmentDtos.AppointmentResponseDto;
import dev.dmsviana.compawny.presentation.dto.appointment.AppointmentDtos.CreateAppointmentRequestDto;
import dev.dmsviana.compawny.presentation.dto.appointment.AppointmentDtos.UpdateAppointmentRequestDto;
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

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Tag(name = "Appointments", description = "API for managing pet care appointments")
@RequestMapping("/api/v1/appointments")
public interface AppointmentApiContract {

    @Operation(
            summary = "Create a new appointment",
            description = """
            Creates a new appointment for a pet with a caregiver.
            Validates:
            - Pet and caregiver existence
            - Caregiver availability
            - Schedule conflicts
            - Minimum scheduling notice (1 hour)
            - Maximum appointment duration (8 hours)
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Appointment created successfully",
                    content = @Content(schema = @Schema(implementation = AppointmentResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pet or caregiver not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Schedule conflict or caregiver unavailable",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping
    @ResponseStatus(CREATED)
    AppointmentResponseDto create(@RequestBody @Valid CreateAppointmentRequestDto request);

    @Operation(
            summary = "Get all appointments",
            description = "Retrieves a paginated list of all appointments. Results can be filtered and sorted."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of appointments retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )
            )
    })
    @GetMapping
    @ResponseStatus(OK)
    Page<AppointmentResponseDto> getAll(
            @ParameterObject
            @PageableDefault(sort = "startTime")
            Pageable pageable
    );

    @Operation(
            summary = "Get appointment by ID",
            description = "Retrieves detailed information about a specific appointment"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointment found",
                    content = @Content(schema = @Schema(implementation = AppointmentResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Appointment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/{id}")
    @ResponseStatus(OK)
    AppointmentResponseDto getById(@PathVariable Long id);

    @Operation(
            summary = "Update appointment",
            description = """
            Updates an existing appointment. Only scheduled appointments can be updated.
            Validates:
            - Schedule conflicts for new time/duration
            - Minimum notice period
            - Maximum duration
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointment updated successfully",
                    content = @Content(schema = @Schema(implementation = AppointmentResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Appointment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Schedule conflict or invalid state transition",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PutMapping("/{id}")
    @ResponseStatus(OK)
    AppointmentResponseDto update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateAppointmentRequestDto request
    );

    @Operation(
            summary = "Start appointment",
            description = "Marks an appointment as in progress. Only scheduled appointments can be started."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointment started successfully",
                    content = @Content(schema = @Schema(implementation = AppointmentResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Appointment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Invalid state transition",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping("/{id}/start")
    @ResponseStatus(OK)
    AppointmentResponseDto startAppointment(@PathVariable Long id);

    @Operation(
            summary = "Complete appointment",
            description = "Marks an appointment as completed. Only in-progress appointments can be completed."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointment completed successfully",
                    content = @Content(schema = @Schema(implementation = AppointmentResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Appointment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Invalid state transition",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping("/{id}/complete")
    @ResponseStatus(OK)
    AppointmentResponseDto completeAppointment(@PathVariable Long id);

    @Operation(
            summary = "Cancel appointment",
            description = "Cancels an appointment. Only scheduled or in-progress appointments can be cancelled."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointment cancelled successfully",
                    content = @Content(schema = @Schema(implementation = AppointmentResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Appointment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Invalid state transition",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping("/{id}/cancel")
    @ResponseStatus(OK)
    AppointmentResponseDto cancelAppointment(@PathVariable Long id);

    @Operation(
            summary = "Get upcoming appointments by caregiver",
            description = "Retrieves all scheduled appointments for a specific caregiver"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of appointments retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AppointmentResponseDto.class)))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Caregiver not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/caregiver/{caregiverId}/upcoming")
    @ResponseStatus(OK)
    List<AppointmentResponseDto> getUpcomingAppointmentsByCaregiver(
            @PathVariable Long caregiverId
    );

    @Operation(
            summary = "Get upcoming appointments by pet",
            description = "Retrieves all scheduled appointments for a specific pet"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of appointments retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AppointmentResponseDto.class)))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pet not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/pet/{petId}/upcoming")
    @ResponseStatus(OK)
    List<AppointmentResponseDto> getUpcomingAppointmentsByPet(
            @PathVariable Long petId
    );
}