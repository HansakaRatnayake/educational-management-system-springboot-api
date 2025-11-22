package com.lezord.system_api.service;

import com.lezord.system_api.dto.request.RequestStudentHasAssignmentDTO;
import com.lezord.system_api.dto.request.RequestStudentHasAssignmentUpdateDTO;
import com.lezord.system_api.dto.response.ResponseStudentHasAssignmentDTO;
import com.lezord.system_api.dto.response.paginate.PaginatedStudentHasAssignmentDTO;
import com.lezord.system_api.entity.enums.StudentHasAssignmentTypes;

public interface StudentHasAssignmentService {
    String createStudentAssignment(RequestStudentHasAssignmentDTO dto);
    void updateStudentAssignment(RequestStudentHasAssignmentUpdateDTO dto, String studentHasAssignmentId, StudentHasAssignmentTypes types);
    ResponseStudentHasAssignmentDTO findByStudentAndAssignmentIds(String studentId,String assignmentId);
    PaginatedStudentHasAssignmentDTO getAllCompletedAssignmentWithStudentMarks(String studentId, String intakeId, String contentTypeId, int page, int size);
}
