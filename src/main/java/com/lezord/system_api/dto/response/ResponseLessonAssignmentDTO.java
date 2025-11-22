package com.lezord.system_api.dto.response;

import com.lezord.system_api.entity.enums.LessonAssignmentStatusTypes;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseLessonAssignmentDTO {
    private String propertyId;
    private String title;
    private String description;
    private int time;
    private int passValue;
    private boolean backwardAvailable;
    private boolean halfMarksForMultipleAnswers;
    private long orderIndex;
    private Boolean finalAssignment;
    private LessonAssignmentStatusTypes statusType;
}
