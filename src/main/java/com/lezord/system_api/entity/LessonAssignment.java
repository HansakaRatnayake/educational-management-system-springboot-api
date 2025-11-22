package com.lezord.system_api.entity;

import com.lezord.system_api.entity.enums.LessonAssignmentStatusTypes;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "lesson_assignment")
public class LessonAssignment {
    @Id
    @Column(name = "property_id")
    private String propertyId;

    @Column(name = "title",length = 225,nullable = false)
    private String title;

    @Column(name = "description",columnDefinition = "TEXT")
    private String description;

    @Column(name = "time",nullable = false)
    private int time;

    @Column(name = "pass_value",nullable = false)
    private int passValue;

    @Column(name = "backward_available",nullable = false, columnDefinition = "TINYINT")
    private Boolean backwardAvailable;

    @Column(name = "half_marks_for_multiple_aswers", columnDefinition = "TINYINT")
    private Boolean halfMarksForMultipleAnswers;

    @Column(name = "is_final_assignment", columnDefinition = "TINYINT")
    private Boolean finalAssignment;

    @Column(name = "order_index",nullable = false, columnDefinition = "BIGINT")
    private long orderIndex;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_type",nullable = false)
    private LessonAssignmentStatusTypes statusType;

    @ManyToOne
    @JoinColumn(name = "lesson_id",nullable = false)
    private CourseStageContent lesson;

    @ManyToOne
    @JoinColumn(name = "intake_id",nullable = false)
    private Intake intake;

    @OneToMany(mappedBy = "lessonAssignment",fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true)
    List<AssignmentQuestion> assignmentQuestions;

    @OneToMany(mappedBy = "lessonAssignment",fetch = FetchType.LAZY)
    List<LessonAssignmentTemp> assignmentTemps;

    @OneToMany(mappedBy = "assignment",fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    Set<StudentHasAssignment> studentHasAssignments;
}
