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
public class ResponseStudentHasAssignmentDTO {
    private String propertyId;
    private StudentHasAssignmentMarksTypes marksType;
    private int passValue;
    private Long fullMarks;
    private Instant updateAt;
    private StudentHasAssignmentTypes displayStatus;
}
