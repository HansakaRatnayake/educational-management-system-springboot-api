package com.lezord.system_api.service.impl;

import com.lezord.system_api.dto.request.RequestAssignmentAnswersDTO;
import com.lezord.system_api.dto.request.RequestAssignmentSubQuestionDTO;
import com.lezord.system_api.dto.response.ResponseAssignmentQuestionAnswerDTO;
import com.lezord.system_api.dto.response.ResponseAssignmentSubQuestionDTO;
import com.lezord.system_api.dto.response.paginate.PaginatedAssignmentSubQuestionDTO;
import com.lezord.system_api.entity.AssignmentQuestion;
import com.lezord.system_api.entity.AssignmentQuestionAnswer;
import com.lezord.system_api.entity.AssignmentSubQuestion;
import com.lezord.system_api.exception.EntryNotFoundException;
import com.lezord.system_api.repository.AssignmentQuestionAnswerRepository;
import com.lezord.system_api.repository.AssignmentQuestionRepository;
import com.lezord.system_api.repository.AssignmentSubQuestionRepository;
import com.lezord.system_api.service.AssignmentSubQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignmentSubQuestionServiceImpl implements AssignmentSubQuestionService {
    private final AssignmentQuestionRepository assignmentQuestionRepository;
    private final AssignmentSubQuestionRepository assignmentSubQuestionRepository;
    private final AssignmentQuestionAnswerRepository assignmentQuestionAnswerRepository;

    @Override
    public void createSubQuestion(RequestAssignmentSubQuestionDTO dto, String questionId) {
        if (dto == null) throw new EntryNotFoundException("Dto data not found");
        if (questionId.isEmpty()) throw new EntryNotFoundException("Question id not found");

        Optional<AssignmentQuestion> selectedQuestion = assignmentQuestionRepository.findById(questionId);
        if (selectedQuestion.isEmpty()) throw new EntryNotFoundException("Question data not found");

        AssignmentSubQuestion savedSubQuestion = assignmentSubQuestionRepository.save(
                AssignmentSubQuestion.builder()
                        .propertyId(UUID.randomUUID().toString())
                        .question(dto.getQuestion())
                        .orderIndex(generateSubQuestionNumber(questionId))
                        .assignmentQuestion(selectedQuestion.get())
                        .build()
        );

        createAnswers(dto.getAnswers(),savedSubQuestion);
    }

    private void createAnswers(List<RequestAssignmentAnswersDTO> answers, AssignmentSubQuestion savedSubQuestion) {
        for (RequestAssignmentAnswersDTO dto:answers){
                    assignmentQuestionAnswerRepository.save(
                            AssignmentQuestionAnswer.builder()
                                    .propertyId(UUID.randomUUID().toString())
                                    .answer(dto.getAnswer())
                                    .isCorrect(dto.getIsCorrect())
                                    .assignmentSubQuestion(savedSubQuestion)
                                    .build()
                    );
        }
    }

    @Override
    public PaginatedAssignmentSubQuestionDTO getAllByQuestionId(String questionId, int page, int size) {
        if (questionId.isEmpty()) throw new EntryNotFoundException("Question Id Not Found");
        if (size < 1) throw new EntryNotFoundException("Page Size must not be less than 1");

        Pageable pageable = PageRequest.of(page,size);

        Page<AssignmentSubQuestion> allData = assignmentSubQuestionRepository.findByAssignmentQuestion_PropertyIdOrderByOrderIndexAsc(questionId, pageable);
        if (allData.isEmpty()) throw new EntryNotFoundException("Sub Question Data Not Found");

        List<ResponseAssignmentSubQuestionDTO> finalData = new ArrayList<>();
        for (AssignmentSubQuestion s : allData) {
            finalData.add(
                    ResponseAssignmentSubQuestionDTO.builder()
                            .propertyId(s.getPropertyId())
                            .question(s.getQuestion())
                            .orderIndex(s.getOrderIndex())
                            .answers(convertToQuestionAnswers(s.getAssignmentQuestionAnswers()))
                            .build()
            );
        }
        return PaginatedAssignmentSubQuestionDTO.builder()
                .count(assignmentSubQuestionRepository.findAllCount(questionId))
                .dataList(finalData)
                .build();
    }

    @Override
    public void deleteSubQuestion(String subQuestionId) {
        if (subQuestionId.isEmpty()) throw new EntryNotFoundException("Sub question id not found");

        Optional<AssignmentSubQuestion> selectedSubQuestion = assignmentSubQuestionRepository.findById(subQuestionId);
        if (selectedSubQuestion.isEmpty()) throw new EntryNotFoundException("Sub Questions data not found");

        long orderIndex = selectedSubQuestion.get().getOrderIndex();
        AssignmentQuestion assignmentQuestion = selectedSubQuestion.get().getAssignmentQuestion();

        assignmentSubQuestionRepository.deleteById(subQuestionId);

        List<AssignmentSubQuestion> updatedSubQuestions = assignmentSubQuestionRepository.findByAssignmentQuestionAndOrderIndexGreaterThanOrderByOrderIndex(assignmentQuestion, Math.toIntExact(orderIndex)).stream().peek(assignmentSubQuestion -> assignmentQuestion.setOrderIndex(assignmentQuestion.getOrderIndex() - 1)).toList();

        assignmentSubQuestionRepository.saveAll(updatedSubQuestions);
    }

    private List<ResponseAssignmentQuestionAnswerDTO> convertToQuestionAnswers(List<AssignmentQuestionAnswer> assignmentQuestionAnswers) {
        List<ResponseAssignmentQuestionAnswerDTO> finalData = new ArrayList<>();
        for (AssignmentQuestionAnswer n : assignmentQuestionAnswers) {
            finalData.add(
                    ResponseAssignmentQuestionAnswerDTO.builder()
                            .propertyId(n.getPropertyId())
                            .answer(n.getAnswer())
                            .isCorrect(n.isCorrect())
                            .build()
            );
        }
        return finalData;
    }

    private long generateSubQuestionNumber(String questionId) {
        int last = assignmentSubQuestionRepository.getLastSubQuestionNumber(questionId);
        return last + 1;
    }
}
