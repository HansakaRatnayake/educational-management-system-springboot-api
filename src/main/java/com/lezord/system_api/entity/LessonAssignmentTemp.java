package com.lezord.system_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Entity
@Table(name = "lesson_assignment_temporary")
public class LessonAssignmentTemp {
    @Id
    @Column(name = "property_id")
    private String propertyId;

    @Column(name = "total_time", nullable = false)
    private Double time;

    @Column(name = "present_time", nullable = false)
    private Double currentTime;

    @Column(name = "current_index",columnDefinition = "TINYINT",nullable = false)
    private Long currentIndex;

    @Column(name = "backward_available",nullable = false, columnDefinition = "TINYINT")
    private Boolean backwardAvailable;

    @Column(name = "half_marks_for_multiple_aswers", columnDefinition = "TINYINT")
    private Boolean halfMarksForMultipleAnswers;

    @Column(name = "is_final_assignment", columnDefinition = "TINYINT")
    private Boolean finalAssignment;

    @Column(name = "created_at",nullable = false)
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "student_id",nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "assignment_id",nullable = false)
    private LessonAssignment lessonAssignment;

    @OneToMany(mappedBy = "lessonAssignmentTemp",fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true)
    List<AssignmentQuestionTemp> assignmentQuestionTemps;
}
