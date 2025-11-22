package com.lezord.system_api.service;

import com.lezord.system_api.dto.response.ResponseStudentAssignmentFailedRequestDTO;
import com.lezord.system_api.dto.response.paginate.PaginatedFailedRequestsDTO;

public interface StudentAssignmentFailedRequestService {
    void createRequest(String studentId,String assignmentId);
    void acceptRequest(String requestId);
    PaginatedFailedRequestsDTO findAllRequestsByCourseAndIntake(String course, String intake, String searchText, int page, int size);
    ResponseStudentAssignmentFailedRequestDTO findByStudentAndAssignmentIds(String studentId,String assignmentId);
}
