package com.lezord.system_api.dto.request;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class RequestLessonAssignmentTempDTO {
    private Boolean backwardAvailable;
    private Boolean halfMarksForMultipleAnswers;
    private Boolean finalAssignment;
    List<RequestAssignmentQuestionTempDTO> assignmentQuestionTemps;
}
