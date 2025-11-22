package com.lezord.system_api.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseInstructorDashboardDetailDTO {

    private ResponseInstructorDashboardStatCardDetailDTO statCards;
    private List<ResponseInstructorDashboardAverageAssigmentMarksDetailDTO> assigmentMarksDetailList;



}
