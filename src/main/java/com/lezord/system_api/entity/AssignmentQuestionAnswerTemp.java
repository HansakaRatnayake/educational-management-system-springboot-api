package com.lezord.system_api.entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "assignment_question_answer_temporary")
public class AssignmentQuestionAnswerTemp {
    @Id
    @Column(name = "property_id")
    private String propertyId;

    @Column(name = "answer",nullable = false)
    private String answer;

    @Column(name = "is_correct",nullable = false, columnDefinition = "TINYINT")
    private boolean isCorrect;

    @Column(name = "is_student_select",nullable = false, columnDefinition = "TINYINT")
    private Boolean isStudentSelect;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "assignment_sub_question_temp_id",nullable = false)
    private AssignmentSubQuestionTemp assignmentSubQuestionTemp;
}
