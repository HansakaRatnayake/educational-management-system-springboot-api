package com.lezord.system_api.repository;

import com.lezord.system_api.entity.LessonAssignmentTemp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonAssignmentTempRepository extends JpaRepository<LessonAssignmentTemp,String> {
    Optional<LessonAssignmentTemp> findFirstByStudentPropertyIdAndLessonAssignmentPropertyIdOrderByCreatedAtDesc(String studentId, String assignmentId);

    List<LessonAssignmentTemp> findAllByLessonAssignmentPropertyId(String assignmentId);

    void deleteAllByLessonAssignmentPropertyId(String assignmentId);
}
