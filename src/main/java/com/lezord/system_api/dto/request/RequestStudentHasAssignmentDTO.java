package com.lezord.system_api.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class RequestStudentHasAssignmentDTO {
    private String studentId;
    private String assignmentId;
}
