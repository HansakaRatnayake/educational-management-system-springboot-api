package com.lezord.system_api.repository;

import com.lezord.system_api.entity.AssignmentQuestionTemp;
import com.lezord.system_api.entity.enums.LessonAssignmentTempDidTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentQuestionTempRepository extends JpaRepository<AssignmentQuestionTemp,String> {
    Optional<AssignmentQuestionTemp> findAllByLessonAssignmentTempPropertyIdAndOrderIndex(String assignmentTempId, int index);

    List<AssignmentQuestionTemp> findAllByLessonAssignmentTempPropertyIdOrderByOrderIndexAsc(String assignmentTempId);

    Optional<AssignmentQuestionTemp> findByLessonAssignmentTempPropertyIdAndOrderIndex(String assignmentTempId, int orderIndex);

    List<AssignmentQuestionTemp> findAllByLessonAssignmentTempPropertyId(String assignmentTempId);

    Optional<AssignmentQuestionTemp> findByLessonAssignmentTempPropertyIdAndFilled(String assignmentTempId, LessonAssignmentTempDidTypes type);
}
