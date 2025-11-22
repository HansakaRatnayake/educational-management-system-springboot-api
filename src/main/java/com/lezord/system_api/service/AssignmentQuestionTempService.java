package com.lezord.system_api.service;

import com.lezord.system_api.dto.request.RequestLessonAssignmentTempDTO;
import com.lezord.system_api.dto.response.ResponseAssignmentQuestionTempDTO;
import com.lezord.system_api.dto.response.ResponseLessonAssignmentTempDTO;
import com.lezord.system_api.dto.response.paginate.PaginatedAssignmentQuestionTempOrderIndexDTO;
import com.lezord.system_api.entity.enums.LessonAssignmentTempDidTypes;

import java.util.concurrent.atomic.AtomicReference;

public interface AssignmentQuestionTempService {
    void createQuestionsTemp(RequestLessonAssignmentTempDTO dto, String studentId, String assignmentId);
    ResponseLessonAssignmentTempDTO getAllAssignmentData(String studentId, String assignmentId);
    ResponseAssignmentQuestionTempDTO getQuestionsOneByOne(String assignmentTempId, int index);
    PaginatedAssignmentQuestionTempOrderIndexDTO getQuestionCount(String assignmentTempId);
    void changeCurrentTime(String assignmentTempId,Double currentTime);
    void changeCurrentIndex(String assignmentTempId,int currentIndex);
    int getCurrentIndex(String assignmentTempId);
    AtomicReference<Double> getAllAssignmentQuestionMarks(String assignmentTempId);
    void changeFilledStatus(String assignmentTempId, int orderIndex, LessonAssignmentTempDidTypes type);
    void changeAnswerSelectState(String answerId);
    void changeAssignmentQuestionMarks(String questionId, Double marks);
    void deleteTempLessonAssignment(String assignmentTempId);
    void changeBookmarkStatus(String assignmentTempQuestionId);
}
