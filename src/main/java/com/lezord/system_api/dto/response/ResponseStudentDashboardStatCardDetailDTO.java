package com.lezord.system_api.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseStudentDashboardStatCardDetailDTO {

    private int programCount;
    private int progress;
    private int stageCount;
    private int assigmentCount;
}
