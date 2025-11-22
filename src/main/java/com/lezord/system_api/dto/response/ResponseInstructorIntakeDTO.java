package com.lezord.system_api.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseInstructorIntakeDTO {

    private String intakeId;
    private String intakeName;
    private int progress;
    private String status;
}
