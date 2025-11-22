package com.lezord.system_api.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseInstructorDashboardStatCardDetailDTO {

    private int assignCoursesCount;
    private int progress;
    private int stagesCount;
    private int assigmentCount;
}
