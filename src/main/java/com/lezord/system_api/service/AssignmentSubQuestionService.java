package com.lezord.system_api.service;

import com.lezord.system_api.dto.request.RequestAssignmentSubQuestionDTO;
import com.lezord.system_api.dto.response.paginate.PaginatedAssignmentSubQuestionDTO;

public interface AssignmentSubQuestionService {

    void createSubQuestion(RequestAssignmentSubQuestionDTO dto, String questionId);
    PaginatedAssignmentSubQuestionDTO getAllByQuestionId(String questionId,int page, int size);

    void deleteSubQuestion(String subQuestionId);
}
