package com.lezord.system_api.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseStudentDashboardCourseOverviewDTO {

    private String courseName;
    private String duration;
    private int progress;
    private List<ResponseStudentDashboardViewInstructorDetailDTO> instructors;
    private String instructorAvatar;
    private int assigmentCount;
    private String status;


}
