package com.lezord.system_api.dto.request;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RequestAssignmentSubQuestionTempDTO {
    private String question;
    private Long orderIndex;
    private List<RequestAssignmentQuestionAnswerTempDTO> assignmentQuestionAnswerTemps;
}
