package com.lezord.system_api.dto.response;


import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseClientViewBasicStatisticsDTO {

    private long activeStudentCount;
    private long totalInstructorsCount;
    private long totalProgramCount;
    private long totalSuccessStoriesCount;
}
