package com.lezord.system_api.entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "assignment_question_answer")
public class AssignmentQuestionAnswer {
    @Id
    @Column(name = "property_id")
    private String propertyId;

    @Column(name = "answer",nullable = false)
    private String answer;

    @Column(name = "is_correct",nullable = false, columnDefinition = "TINYINT")
    private boolean isCorrect;

    @ManyToOne
    @JoinColumn(name = "assignment_sub_question_id",nullable = false)
    private AssignmentSubQuestion assignmentSubQuestion;
}
