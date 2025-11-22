package com.lezord.system_api.repository;

import com.lezord.system_api.entity.StudentAssignmentFailedRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentAssignmentFailedRequestRepository extends JpaRepository<StudentAssignmentFailedRequest,String> {
    Optional<StudentAssignmentFailedRequest> findFirstByStudentPropertyIdAndLessonAssignmentPropertyIdOrderByCreatedAtDesc(String studentId, String assignmentId);
    List<StudentAssignmentFailedRequest> findAllByStudentPropertyIdAndIsRequestAccepted(String studentId, Boolean isAccept);
}
