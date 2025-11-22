package com.lezord.system_api.entity.core;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Embeddable
public class StudentAssignmentId implements Serializable {
    private String studentId;
    private String assignmentId;
}
