package com.lezord.system_api.dto.response;

import com.lezord.system_api.entity.enums.LessonAssignmentTempDidTypes;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseAssignmentQuestionTempDTO {
    private String propertyId;
    private String assignmentQuestionImage;
    private String assignmentQuestionRecording;
    private String paragraph;
    private Long orderIndex;
    private Double marks;
    private Boolean isBookmark;
    private LessonAssignmentTempDidTypes filled;
    private List<ResponseAssignmentSubQuestionTempDTO> assignmentSubQuestionTemps;
}
