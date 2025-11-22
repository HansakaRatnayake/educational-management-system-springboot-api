package com.lezord.system_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@ToString
@Table(name = "assignment_question")
public class AssignmentQuestion {
    @Id
    @Column(name = "property_id")
    private String propertyId;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "assignment_question_image_id")
    private AssignmentQuestionImage assignmentQuestionImage;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "assignment_question_audio_id")
    private AssignmentQuestionRecording assignmentQuestionRecording;

    @Column(name = "paragraph",columnDefinition = "TEXT")
    private String paragraph;

    @Column(name = "order_index",columnDefinition = "BIGINT")
    private Long orderIndex;

    @ManyToOne
    @JoinColumn(name = "lesson_assignment_id",nullable = false)
    private LessonAssignment lessonAssignment;

    @OneToMany(mappedBy = "assignmentQuestion",fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<AssignmentSubQuestion> assignmentSubQuestions;

}
