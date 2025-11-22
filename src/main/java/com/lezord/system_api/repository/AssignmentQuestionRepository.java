package com.lezord.system_api.repository;

import com.lezord.system_api.entity.AssignmentQuestion;
import com.lezord.system_api.entity.LessonAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentQuestionRepository extends JpaRepository<AssignmentQuestion, String> {

    @Query(value = "SELECT COUNT(property_id) FROM assignment_question WHERE lesson_assignment_id = ?1",nativeQuery = true)
    Long findAllCount(String assignmentId);
    Page<AssignmentQuestion> findByLessonAssignmentPropertyIdOrderByOrderIndexAsc(String assigmentId, Pageable pageable);

    @Query("SELECT COALESCE(MAX(cs.orderIndex), 0) FROM AssignmentQuestion cs WHERE cs.lessonAssignment.propertyId = :assignmentId")
    long getLastAssignmentQuestionNumber(@Param("assignmentId") String assignmentId);

    List<AssignmentQuestion> findByLessonAssignmentAndOrderIndexGreaterThanOrderByOrderIndex(LessonAssignment lessonAssignment, int orderIndexIsGreaterThan);

    List<AssignmentQuestion> findAllByLessonAssignmentPropertyId(String assignmentId);
}
