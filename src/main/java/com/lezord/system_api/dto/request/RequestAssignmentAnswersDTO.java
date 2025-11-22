package com.lezord.system_api.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RequestAssignmentAnswersDTO {
    private String answer;
    private Boolean isCorrect;
    private Boolean isStudentSelect;
}
