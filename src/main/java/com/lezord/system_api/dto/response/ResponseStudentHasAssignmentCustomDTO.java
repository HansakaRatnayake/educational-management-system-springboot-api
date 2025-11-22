package com.lezord.system_api.dto.response;

import com.lezord.system_api.entity.enums.StudentHasAssignmentMarksTypes;
import com.lezord.system_api.entity.enums.StudentHasAssignmentTypes;
import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseStudentHasAssignmentCustomDTO {
    private String propertyId;
    private ResponseLessonAssignmentDTO assignment;
    private String studentId;
    private Instant createdAt;
    private Instant updateAt;
    private int passValue;
    private StudentHasAssignmentMarksTypes marksType;
    private StudentHasAssignmentTypes statusType;
    private Long fullMarks;
}
