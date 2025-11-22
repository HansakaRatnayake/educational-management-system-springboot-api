package com.lezord.system_api.service;

import com.lezord.system_api.dto.request.RequestLessonAssignmentDTO;
import com.lezord.system_api.dto.response.ResponseLessonAssignmentDTO;
import com.lezord.system_api.dto.response.paginate.PaginatedLessonAssignmentDTO;
import com.lezord.system_api.entity.enums.LessonAssignmentStatusTypes;

public interface LessonAssignmentService {
    String createAssignment(RequestLessonAssignmentDTO dto, String lessonId, String intakeId);
    void updateAssignment(RequestLessonAssignmentDTO dto, String assignmentId);
    void changeStatus(LessonAssignmentStatusTypes statusType, String assignmentId);
    void deleteAssignment(String assignmentId);
    ResponseLessonAssignmentDTO findById(String assignmentId);
    String getIntakeIdByCourseAndStudentIds(String courseId, String studentId);
    PaginatedLessonAssignmentDTO getAllByLessonIdAndIntake(String lessonId, String intake, String studentId, int page, int size, Boolean areOnlyActivated);
}
