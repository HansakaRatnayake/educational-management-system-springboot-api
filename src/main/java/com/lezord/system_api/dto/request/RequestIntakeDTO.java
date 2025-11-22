package com.lezord.system_api.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lezord.system_api.validations.anno.ValidIntakeDates;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@ValidIntakeDates
@NoArgsConstructor
@AllArgsConstructor
public class RequestIntakeDTO {

    @NotBlank(message = "name required")
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "intakeEndDate required")
    @Future(message = "Date of intakeStartDate must be in the future")
    private LocalDate intakeStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "intakeEndDate required")
    @Future(message = "Date of intakeEndDate must be in the future")
    private LocalDate intakeEndDate;

    @NotNull(message = "Available seats is required")
    @Min(value = 1, message = "Available seats must be at least 1")
    private int availableSeats;

    @NotBlank(message = "course required")
    private String courseId;

    @NotNull(message = "Price is required")
    @Min(value = 1, message = "price must be at least 1")
    private BigDecimal price;

}
