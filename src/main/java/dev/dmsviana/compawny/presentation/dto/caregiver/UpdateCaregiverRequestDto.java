package dev.dmsviana.compawny.presentation.dto.caregiver;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCaregiverRequestDto {

    @Pattern(regexp = "^\\(\\d{2}\\)\\s\\d{5}-\\d{4}$", message = "{caregiver.phone.pattern}")
    private String phone;

    @Size(max = 500, message = "{caregiver.description.size}")
    private String description;

    @DecimalMin(value = "0.0", message = "{caregiver.hourlyRate.min}")
    @Digits(integer = 8, fraction = 2, message = "{caregiver.hourlyRate.digits}")
    private BigDecimal hourlyRate;
}