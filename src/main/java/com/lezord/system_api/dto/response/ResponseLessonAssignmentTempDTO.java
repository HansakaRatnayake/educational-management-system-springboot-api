package com.lezord.system_api.dto.response;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ResponseLessonAssignmentTempDTO {
    private String propertyId;
    private Double time;
    private Double currentTime;
    private Long currentIndex;
    private Boolean backwardAvailable;
    private Boolean halfMarksForMultipleAnswers;
    private Boolean finalAssignment;
    List<ResponseAssignmentQuestionTempDTO> assignmentQuestionTemps;
}
