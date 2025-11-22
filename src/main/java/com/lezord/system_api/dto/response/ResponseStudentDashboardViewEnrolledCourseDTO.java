package com.lezord.system_api.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseStudentDashboardViewEnrolledCourseDTO {

    private String intakeId;
    private String intakeName;
    private String courseName;
    private String courseId;


}
