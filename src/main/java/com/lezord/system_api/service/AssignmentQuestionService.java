package com.lezord.system_api.service;

import com.lezord.system_api.dto.request.RequestAssignmentQuestionDTO;
import com.lezord.system_api.dto.response.ResponseAssignmentQuestionDTO;
import com.lezord.system_api.dto.response.paginate.PaginatedAssignmentQuestionDTO;

public interface AssignmentQuestionService {
    String createQuestions(RequestAssignmentQuestionDTO dto, String assignmentId);

    void updateQuestion(RequestAssignmentQuestionDTO dto, String questionId);

    PaginatedAssignmentQuestionDTO getAllQuestionsByAssignmentId(String assignmentId,int page,int size);

    PaginatedAssignmentQuestionDTO getAllQuestionsByAssignmentIdWithoutPagination(String assignmentId);

    ResponseAssignmentQuestionDTO findById(String questionId);

    void deleteAssignmentQuestion(String questionId);
}
