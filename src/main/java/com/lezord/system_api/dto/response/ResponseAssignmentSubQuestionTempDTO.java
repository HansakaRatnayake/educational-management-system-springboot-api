package com.lezord.system_api.dto.response;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ResponseAssignmentSubQuestionTempDTO {
    private String propertyId;
    private String question;
    private Long orderIndex;
    private List<ResponseAssignmentQuestionAnswerTempDTO> assignmentQuestionAnswerTemps;
}
