package com.lezord.system_api.dto.response;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseAssignmentSubQuestionDTO {
    private String propertyId;
    private String question;
    private Long orderIndex;
    private List<ResponseAssignmentQuestionAnswerDTO> answers;
}
