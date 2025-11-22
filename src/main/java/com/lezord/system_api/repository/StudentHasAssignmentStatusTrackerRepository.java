package com.lezord.system_api.repository;

import com.lezord.system_api.entity.StudentHasAssignmentStatusTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentHasAssignmentStatusTrackerRepository extends JpaRepository<StudentHasAssignmentStatusTracker,String> {
Optional<StudentHasAssignmentStatusTracker> findByStudentAssignmentIdStudentIdAndStudentAssignmentIdAssignmentId(String studentId,String assignmentId);
}
