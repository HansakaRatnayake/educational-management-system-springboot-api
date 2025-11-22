package com.lezord.system_api.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestInstructorIntakeAssignationDTO {

    private String instructorId;
    private String intakeId;

}
