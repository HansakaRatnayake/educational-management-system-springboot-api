package com.lezord.system_api.dto.request;

import com.lezord.system_api.entity.enums.StudentHasAssignmentMarksTypes;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RequestStudentHasAssignmentUpdateDTO {
    private Long fullMarks;
    private StudentHasAssignmentMarksTypes marksType;
}
