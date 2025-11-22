package com.lezord.system_api.repository;

import com.lezord.system_api.entity.AssignmentQuestion;
import com.lezord.system_api.entity.AssignmentSubQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentSubQuestionRepository extends JpaRepository<AssignmentSubQuestion, String> {
    @Query(value = "SELECT * FROM assignment_sub_question WHERE assignment_question_id = ?1", nativeQuery = true)
    List<AssignmentSubQuestion> findAllByAssignmentQuestionId(String questionId, Pageable pageable);

    @Query(value = "SELECT COUNT(property_id) FROM assignment_sub_question WHERE assignment_question_id = ?1",nativeQuery = true)
    Long findAllCount(String questionId);

    @Query("SELECT COALESCE(MAX(cs.orderIndex), 0) FROM AssignmentSubQuestion cs WHERE cs.assignmentQuestion.propertyId = :questionId")
    int getLastSubQuestionNumber(@Param("questionId") String questionId);

    Page<AssignmentSubQuestion> findByAssignmentQuestion_PropertyIdOrderByOrderIndexAsc(String questionId, Pageable pageable);
    List<AssignmentSubQuestion> findByAssignmentQuestionAndOrderIndexGreaterThanOrderByOrderIndex(AssignmentQuestion assignmentQuestion, int orderIndexIsGreaterThan);
}
