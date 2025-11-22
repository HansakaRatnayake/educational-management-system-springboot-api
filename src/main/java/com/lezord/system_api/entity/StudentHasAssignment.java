package com.lezord.system_api.entity;

import com.lezord.system_api.entity.enums.StudentHasAssignmentMarksTypes;
import com.lezord.system_api.entity.enums.StudentHasAssignmentTypes;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "student_has_assignment")
public class StudentHasAssignment {
    @Id
    private String propertyId;

    @ManyToOne()
    @JoinColumn(name = "student_id",nullable = false)
    private Student student;

    @ManyToOne()
    @JoinColumn(name = "assignment_id",nullable = false)
    private LessonAssignment assignment;

    @Column(name = "created_at",nullable = false)
    private Instant createdAt;


    @Column(name = "update_at")
    private Instant updateAt;

    @Column(name = "pass_value")
    private int passValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "mark_type",nullable = false)
    private StudentHasAssignmentMarksTypes marksType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_type",nullable = false)
    private StudentHasAssignmentTypes statusType;

    @Column(name = "full_marks",columnDefinition = "BIGINT")
    private Long fullMarks;
}
