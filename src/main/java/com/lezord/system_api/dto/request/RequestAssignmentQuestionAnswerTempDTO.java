package com.lezord.system_api.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RequestAssignmentQuestionAnswerTempDTO {
    private String answer;
    private Boolean isCorrect;
    private Boolean isStudentSelect;
}
