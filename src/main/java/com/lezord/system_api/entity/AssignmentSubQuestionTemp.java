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
@Table(name = "assignment_sub_question_temporary")
public class AssignmentSubQuestionTemp {
    @Id
    @Column(name = "property_id")
    private String propertyId;

    @Column(name = "question")
    private String question;

    @Column(name = "order_index",nullable = false, columnDefinition = "BIGINT")
    private Long orderIndex;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "assignment_question_temp_id",nullable = false)
    private AssignmentQuestionTemp assignmentQuestionTemp;

    @OneToMany(mappedBy = "assignmentSubQuestionTemp",fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssignmentQuestionAnswerTemp> assignmentQuestionAnswerTemps;
}
