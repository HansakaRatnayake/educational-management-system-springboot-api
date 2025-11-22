package com.lezord.system_api.dto.response;

import com.lezord.system_api.entity.enums.EmploymentType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseEmploymentDetailDTO {


    private EmploymentType employmentType;

    private String designation;

    private LocalDate dateJoined;

    private Boolean activeStatus;


}
