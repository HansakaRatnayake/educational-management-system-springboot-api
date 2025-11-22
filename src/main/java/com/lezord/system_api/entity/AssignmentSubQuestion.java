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
@Table(name = "assignment_sub_question")
public class AssignmentSubQuestion {
    @Id
    @Column(name = "property_id")
    private String propertyId;

    @Column(name = "question")
    private String question;

    @Column(name = "order_index",nullable = false, columnDefinition = "BIGINT")
    private Long orderIndex;

    @ManyToOne
    @JoinColumn(name = "assignment_question_id",nullable = false)
    private AssignmentQuestion assignmentQuestion;

    @OneToMany(mappedBy = "assignmentSubQuestion",fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssignmentQuestionAnswer> assignmentQuestionAnswers;
}
