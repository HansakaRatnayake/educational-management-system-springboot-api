package com.lezord.system_api.dto.request;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RequestAssignmentQuestionTempDTO {
    private String assignmentQuestionImage;
    private String assignmentQuestionRecording;
    private String paragraph;
    private Long orderIndex;
    private List<RequestAssignmentSubQuestionDTO> assignmentSubQuestionTemps;
}
