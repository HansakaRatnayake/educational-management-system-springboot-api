package com.lezord.system_api.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseStudentDashboardViewInstructorDetailDTO {
    private String instructorId;
    private String instructorName;
    private String instructorAvatar;
}
