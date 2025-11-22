package com.lezord.system_api.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ResponseAssignmentQuestionAnswerTempDTO {
    private String propertyId;
    private String answer;
    private Boolean isCorrect;
    private Boolean isStudentSelect;
}
