package com.lezord.system_api.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseStudentDashboardDetailDTO {

    private ResponseStudentDashboardStatCardDetailDTO statCards;
    private List<ResponseStudentDashboardAssigmentMarksDetailDTO> assigmentMarksDetailList;
    private ResponseStudentDashboardCourseOverviewDTO courseOverview;


}
