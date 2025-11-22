package com.lezord.system_api.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseAssignmentQuestionAnswerDTO {
    private String propertyId;
    private String answer;
    private Boolean isCorrect;
}
