package dev.dmsviana.compawny.presentation.dto.caregiver;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.br.CPF;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCaregiverRequestDto {


    @NotBlank(message = "{caregiver.name.notblank}")
    @Size(min = 3, max = 100, message = "{caregiver.name.size}")
    private String name;

    @CPF(message = "{caregiver.cpf.invalid}")
    @NotBlank(message = "{caregiver.cpf.notblank}")
    private String cpf;

    @Email(message = "{caregiver.email.invalid}")
    @NotBlank(message = "{caregiver.email.notblank}")
    private String email;

    @NotBlank(message = "{caregiver.phone.notblank}")
    @Pattern(regexp = "^\\(\\d{2}\\)\\s\\d{5}-\\d{4}$", message = "{caregiver.phone.pattern}")
    private String phone;

    @Size(max = 500, message = "{caregiver.description.size}")
    private String description;

    @NotNull(message = "{caregiver.hourlyRate.notnull}")
    @DecimalMin(value = "0.0", message = "{caregiver.hourlyRate.min}")
    @Digits(integer = 8, fraction = 2, message = "{caregiver.hourlyRate.digits}")
    private BigDecimal hourlyRate;
}