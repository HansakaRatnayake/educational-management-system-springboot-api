package com.lezord.system_api.entity;

import com.lezord.system_api.entity.core.StudentAssignmentId;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "student_has_assignment_tracker")
public class StudentHasAssignmentStatusTracker {
    @EmbeddedId
    private StudentAssignmentId studentAssignmentId;

    @Column(name = "is_repeated_accepted",columnDefinition = "TINYINT",nullable = false)
    private Boolean isRepeatedAccepted;

    @Column(name = "attemps_count")
    private Long attemps;
}
