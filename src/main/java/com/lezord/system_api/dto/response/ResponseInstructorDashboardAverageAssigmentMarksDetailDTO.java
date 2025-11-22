package com.lezord.system_api.dto.response;

import com.lezord.system_api.dto.response.util.AssignmentMarksStatsResponse;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseInstructorDashboardAverageAssigmentMarksDetailDTO {

    private String assigmentType;
    private AssignmentMarksStatsResponse marks;


}
