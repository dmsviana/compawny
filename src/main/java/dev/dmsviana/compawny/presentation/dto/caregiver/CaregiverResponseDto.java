package dev.dmsviana.compawny.presentation.dto.caregiver;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaregiverResponseDto {


    private Long id;
    private String name;
    private String email;
    private String phone;
    private String description;
    private BigDecimal hourlyRate;
    private Boolean available;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}