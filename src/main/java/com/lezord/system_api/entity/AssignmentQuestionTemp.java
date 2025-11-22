package com.lezord.system_api.entity;

import com.lezord.system_api.entity.enums.LessonAssignmentTempDidTypes;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "assignment_question_temporary")
public class AssignmentQuestionTemp {
    @Id
    @Column(name = "property_id")
    private String propertyId;

    @Column(name = "question_image")
    private String assignmentQuestionImage;

    @Column(name = "question_audio")
    private String assignmentQuestionRecording;

    @Column(name = "paragraph",columnDefinition = "TEXT")
    private String paragraph;

    @Column(name = "order_index",columnDefinition = "BIGINT")
    private Long orderIndex;

    @Column(name = "marks")
    private Double marks;

    @Column(name = "isBookmark",columnDefinition = "TINYINT",nullable = false)
    private Boolean isBookmark;

    @Enumerated(EnumType.STRING)
    @Column(name = "filled",nullable = false)
    private LessonAssignmentTempDidTypes filled;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "lesson_assignment_temporary_id",nullable = false)
    private LessonAssignmentTemp lessonAssignmentTemp;

    @OneToMany(mappedBy = "assignmentQuestionTemp",fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<AssignmentSubQuestionTemp> assignmentSubQuestionTemps;
}
