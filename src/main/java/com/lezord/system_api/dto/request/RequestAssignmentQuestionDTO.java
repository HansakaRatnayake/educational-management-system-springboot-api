package com.lezord.system_api.dto.request;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RequestAssignmentQuestionDTO {
    private String paragraph;
    private List<RequestAssignmentSubQuestionDTO> subQuestions;
}
