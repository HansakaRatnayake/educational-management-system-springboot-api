package com.lezord.system_api.dto.request;

import com.lezord.system_api.entity.enums.EmploymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestEmploymentDetailDTO {

    @NotNull(message = "employee type required")
    private EmploymentType employmentType; // e.g., Full-Time, Part-Time

    @NotBlank(message = "designation required")
    private String designation;

}
