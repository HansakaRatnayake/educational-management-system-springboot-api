package com.lezord.system_api.dto.response;

import com.lezord.system_api.entity.enums.LessonAssignmentTempDidTypes;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseAssignmentQuestionTempOrderIndexDTO {
    private String propertyId;
    private int orderIndex;
    private Boolean isBookmark;
    private LessonAssignmentTempDidTypes filled;
}
