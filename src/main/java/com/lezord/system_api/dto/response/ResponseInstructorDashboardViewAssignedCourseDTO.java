package com.lezord.system_api.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseInstructorDashboardViewAssignedCourseDTO {

    private String intakeId;
    private String intakeName;
    private String courseName;
    private String courseId;


}
