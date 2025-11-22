package com.lezord.system_api.dto.request;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RequestAssignmentSubQuestionDTO {
    private String question;
    private Long orderIndex;
    private List<RequestAssignmentAnswersDTO> answers;
}
