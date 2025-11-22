package com.lezord.system_api.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseStudentCourseEnrollmentEligibilityDTO {

    private boolean eligible;
    private ResponseCourseDTO responseCourse;
    private Boolean enrolled;
}
