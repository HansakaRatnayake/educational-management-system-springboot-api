package com.lezord.system_api.dto.response;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseAssignmentQuestionDTO {
    private String propertyId;
    private ResponseAssignmentQuestionImageDTO assignmentQuestionImage;
    private ResponseAssignmentQuestionAudioDTO assignmentQuestionRecording;
    private String paragraph;
    private long orderIndex;
    private List<ResponseAssignmentSubQuestionDTO> subQuestions;
}
