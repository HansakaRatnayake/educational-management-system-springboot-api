package com.lezord.system_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "student_assignment_failed_request")
public class StudentAssignmentFailedRequest {
    @Id
    private String propertyId;

    @Column(name = "is_request_accepted",columnDefinition = "TINYINT")
    private Boolean isRequestAccepted;

    @Column(name = "created_at",nullable = false)
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "student_id",nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "assignment_id",nullable = false)
    private LessonAssignment lessonAssignment;
}
