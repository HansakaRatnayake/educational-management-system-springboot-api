package com.lezord.system_api.service.impl;

import com.lezord.system_api.dto.request.RequestAssignmentAnswersDTO;
import com.lezord.system_api.dto.request.RequestAssignmentQuestionDTO;
import com.lezord.system_api.dto.request.RequestAssignmentSubQuestionDTO;
import com.lezord.system_api.dto.response.*;
import com.lezord.system_api.entity.*;
import com.lezord.system_api.repository.AssignmentQuestionAnswerRepository;
import com.lezord.system_api.repository.AssignmentQuestionRepository;
import com.lezord.system_api.repository.AssignmentSubQuestionRepository;
import com.lezord.system_api.repository.LessonAssignmentRepository;
import com.nozomi.system_api.dto.response.*;
import com.lezord.system_api.dto.response.paginate.PaginatedAssignmentQuestionDTO;
import com.nozomi.system_api.entity.*;
import com.lezord.system_api.exception.EntryNotFoundException;
import com.nozomi.system_api.repository.*;
import com.lezord.system_api.service.AssignmentQuestionService;
import com.lezord.system_api.util.FileDataHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignmentQuestionServiceImpl implements AssignmentQuestionService {
    private final LessonAssignmentRepository lessonAssignmentRepository;
    private final AssignmentQuestionRepository assignmentQuestionRepository;
    private final AssignmentSubQuestionRepository assignmentSubQuestionRepository;
    private final AssignmentQuestionAnswerRepository assignmentQuestionAswerRepository;
    private final FileDataHandler fileDataHandler;


    @Override
    public String createQuestions(RequestAssignmentQuestionDTO dto, String assignmentId) {
        if (dto == null) throw new EntryNotFoundException("Dto Not Found");
        if (assignmentId.isEmpty()) throw new EntryNotFoundException("Assignment Id Not Found");

        Optional<LessonAssignment> selectedAssignment = lessonAssignmentRepository.findById(assignmentId);
        if (selectedAssignment.isEmpty())
            throw new EntryNotFoundException("Assignment Data Not Found And Please Check Your Assignment Id");

        AssignmentQuestion assignmentQuestion = AssignmentQuestion.builder()
                .propertyId(UUID.randomUUID().toString())
                .paragraph(dto.getParagraph())
                .lessonAssignment(selectedAssignment.get())
                .orderIndex(generateAssignmentQuestionNumber(assignmentId))
                .build();

        AssignmentQuestion createdQuestion = assignmentQuestionRepository.save(assignmentQuestion);

        createSubQuestions(createdQuestion, dto);

        return createdQuestion.getPropertyId();

    }

    @Transactional
    @Override
    public void updateQuestion(RequestAssignmentQuestionDTO dto, String questionId) {
        if (dto == null) throw new EntryNotFoundException("Dto data not found");
        if (questionId.isEmpty()) throw new EntryNotFoundException("Question Id not found");

        AssignmentQuestion question = assignmentQuestionRepository.findById(questionId)
                .orElseThrow(() -> new EntryNotFoundException("Question data not found"));


        question.setParagraph(dto.getParagraph());


        question.getAssignmentSubQuestions().clear();


        for (RequestAssignmentSubQuestionDTO subDto : dto.getSubQuestions()) {
            AssignmentSubQuestion subQuestion = new AssignmentSubQuestion();
            subQuestion.setPropertyId(UUID.randomUUID().toString());
            subQuestion.setQuestion(subDto.getQuestion());
            subQuestion.setAssignmentQuestion(question);
            subQuestion.setOrderIndex(generateAssignmentSubQuestionNumber(questionId));


            List<AssignmentQuestionAnswer> answers = new ArrayList<>();
            for (RequestAssignmentAnswersDTO answerDTO : subDto.getAnswers()) {
                AssignmentQuestionAnswer answer = new AssignmentQuestionAnswer();
                answer.setPropertyId(UUID.randomUUID().toString());
                answer.setAnswer(answerDTO.getAnswer());
                answer.setCorrect(Boolean.TRUE.equals(answerDTO.getIsCorrect()));
                answer.setAssignmentSubQuestion(subQuestion);
                answers.add(answer);
            }

            subQuestion.setAssignmentQuestionAnswers(answers);
            question.getAssignmentSubQuestions().add(subQuestion);
        }

        assignmentQuestionRepository.save(question);
    }



    @Override
    public PaginatedAssignmentQuestionDTO getAllQuestionsByAssignmentId(String assignmentId,int page, int size) {
        if (size < 1) throw new EntryNotFoundException("Page Size Must Not Be Less Than 1");

        Pageable pageable = PageRequest.of(page,size);
        Page<AssignmentQuestion> allData = assignmentQuestionRepository.findByLessonAssignmentPropertyIdOrderByOrderIndexAsc(assignmentId, pageable);

        List<ResponseAssignmentQuestionDTO> finalAssignmentQuestions = new ArrayList<>();
        for (AssignmentQuestion s : allData) {
            System.out.println(s.getPropertyId());
            finalAssignmentQuestions.add(
                    ResponseAssignmentQuestionDTO.builder()
                            .propertyId(s.getPropertyId())
                            .assignmentQuestionImage(
                                    s.getAssignmentQuestionImage() !=null ? convertToQuestionImage(s.getAssignmentQuestionImage()) : null
                            )
                            .assignmentQuestionRecording(
                                    s.getAssignmentQuestionRecording() != null ? convertToQuestionAudio(s.getAssignmentQuestionRecording()) : null
                            )
                            .paragraph(s.getParagraph())
                            .orderIndex(s.getOrderIndex())
                            .subQuestions(convertToSubQuestionDto(s.getAssignmentSubQuestions()))
                            .build()
            );
        }
        return PaginatedAssignmentQuestionDTO.builder()
                .count(assignmentQuestionRepository.findAllCount(assignmentId))
                .dataList(finalAssignmentQuestions)
                .build();
    }

    @Override
    public PaginatedAssignmentQuestionDTO getAllQuestionsByAssignmentIdWithoutPagination(String assignmentId) {
        if (assignmentId.isEmpty()) throw new EntryNotFoundException("Assignment id not found");

        List<AssignmentQuestion> allData = assignmentQuestionRepository.findAllByLessonAssignmentPropertyId(assignmentId);
        if (allData.isEmpty()) return PaginatedAssignmentQuestionDTO.builder()
                .count(0)
                .dataList(null)
                .build();

        List<ResponseAssignmentQuestionDTO> list = new ArrayList<>();

        allData.forEach(s->{
            list.add(
                    ResponseAssignmentQuestionDTO.builder()
                            .propertyId(s.getPropertyId())
                            .assignmentQuestionImage(
                                    s.getAssignmentQuestionImage() !=null ? convertToQuestionImage(s.getAssignmentQuestionImage()) : null
                            )
                            .assignmentQuestionRecording(
                                    s.getAssignmentQuestionRecording() != null ? convertToQuestionAudio(s.getAssignmentQuestionRecording()) : null
                            )
                            .paragraph(s.getParagraph())
                            .orderIndex(s.getOrderIndex())
                            .subQuestions(convertToSubQuestionDto(s.getAssignmentSubQuestions()))
                            .build()
            );
        });

        return PaginatedAssignmentQuestionDTO.builder()
                .count(allData.size())
                .dataList(list)
                .build();
    }

    @Override
    public ResponseAssignmentQuestionDTO findById(String questionId) {
        if (questionId.isEmpty()) throw new EntryNotFoundException("Question id not found");

        Optional<AssignmentQuestion> selectedQuestion = assignmentQuestionRepository.findById(questionId);
        if (selectedQuestion.isEmpty()) throw new EntryNotFoundException("Question data not found");

        return ResponseAssignmentQuestionDTO.builder()
                .propertyId(selectedQuestion.get().getPropertyId())
                .assignmentQuestionImage(
                        selectedQuestion.get().getAssignmentQuestionImage() != null ? convertToImageDto(selectedQuestion.get().getAssignmentQuestionImage()) : null
                )
                .assignmentQuestionRecording(
                        selectedQuestion.get().getAssignmentQuestionRecording() != null ? convertToAudioDto(selectedQuestion.get().getAssignmentQuestionRecording()) : null
                )
                .paragraph(selectedQuestion.get().getParagraph())
                .orderIndex(selectedQuestion.get().getOrderIndex())
                .subQuestions(convertToSubQuestionDto(selectedQuestion.get().getAssignmentSubQuestions()))
                .build();
    }

    private ResponseAssignmentQuestionAudioDTO convertToAudioDto(AssignmentQuestionRecording assignmentQuestionRecording) {
        return ResponseAssignmentQuestionAudioDTO.builder()
                .propertyId(assignmentQuestionRecording.getPropertyId())
                .resourceUrl(fileDataHandler.byteArrayToString(assignmentQuestionRecording.getResourceUrl()))
                .build();
    }

    private ResponseAssignmentQuestionImageDTO convertToImageDto(AssignmentQuestionImage assignmentQuestionImage) {
        return ResponseAssignmentQuestionImageDTO.builder()
                .propertyId(assignmentQuestionImage.getPropertyId())
                .resourceUrl(fileDataHandler.byteArrayToString(assignmentQuestionImage.getResourceUrl()))
                .build();
    }

    private List<ResponseAssignmentSubQuestionDTO> convertToSubQuestionDto(List<AssignmentSubQuestion> assignmentSubQuestions) {
        List<ResponseAssignmentSubQuestionDTO> list = new ArrayList<>();
        for (AssignmentSubQuestion s:assignmentSubQuestions){
            list.add(
                    ResponseAssignmentSubQuestionDTO.builder()
                            .propertyId(s.getPropertyId())
                            .question(s.getQuestion())
                            .orderIndex(s.getOrderIndex())
                            .answers(convertToQuestionAnswersDto(s.getAssignmentQuestionAnswers()))
                            .build()
            );
        }
        return list;
    }

    private List<ResponseAssignmentQuestionAnswerDTO> convertToQuestionAnswersDto(List<AssignmentQuestionAnswer> assignmentQuestionAnswers) {
        List<ResponseAssignmentQuestionAnswerDTO> list = new ArrayList<>();
        for (AssignmentQuestionAnswer n:assignmentQuestionAnswers){
            list.add(
                    ResponseAssignmentQuestionAnswerDTO.builder()
                            .propertyId(n.getPropertyId())
                            .answer(n.getAnswer())
                            .isCorrect(n.isCorrect())
                            .build()
            );
        }
        return list;
    }

    @Override
    public void deleteAssignmentQuestion(String questionId) {
        if (questionId.isEmpty()) throw new EntryNotFoundException("Question id not found");

        Optional<AssignmentQuestion> selectedQuestion = assignmentQuestionRepository.findById(questionId);
        if (selectedQuestion.isEmpty()) throw new EntryNotFoundException("Question Data not found");

        long orderIndex = selectedQuestion.get().getOrderIndex();
        LessonAssignment lessonAssignment = selectedQuestion.get().getLessonAssignment();

        assignmentQuestionRepository.deleteById(questionId);

        List<AssignmentQuestion> updatedLessonAssignmentList = assignmentQuestionRepository.findByLessonAssignmentAndOrderIndexGreaterThanOrderByOrderIndex(lessonAssignment, Math.toIntExact(orderIndex)).stream().peek(assignmentQuestion -> assignmentQuestion.setOrderIndex(assignmentQuestion.getOrderIndex() - 1)).toList();

        assignmentQuestionRepository.saveAll(updatedLessonAssignmentList);
    }

    private ResponseAssignmentQuestionImageDTO convertToQuestionImage(AssignmentQuestionImage assignmentQuestionImage) {
        return ResponseAssignmentQuestionImageDTO.builder()
                .propertyId(assignmentQuestionImage.getPropertyId())
                .resourceUrl(fileDataHandler.byteArrayToString(assignmentQuestionImage.getResourceUrl()))
                .build();
    }

    private ResponseAssignmentQuestionAudioDTO convertToQuestionAudio(AssignmentQuestionRecording assignmentQuestionRecording) {
        return ResponseAssignmentQuestionAudioDTO.builder()
                .propertyId(assignmentQuestionRecording.getPropertyId())
                .resourceUrl(fileDataHandler.byteArrayToString(assignmentQuestionRecording.getResourceUrl()))
                .build();
    }


    private void createSubQuestions(AssignmentQuestion assignmentQuestion, RequestAssignmentQuestionDTO dto) {
        for (RequestAssignmentSubQuestionDTO subQuestionDTO : dto.getSubQuestions()) {
            AssignmentSubQuestion createdSubQuestion = assignmentSubQuestionRepository.save(
                    AssignmentSubQuestion.builder()
                            .propertyId(UUID.randomUUID().toString())
                            .question(subQuestionDTO.getQuestion())
                            .assignmentQuestion(assignmentQuestion)
                            .orderIndex(generateAssignmentSubQuestionNumber(assignmentQuestion.getPropertyId()))
                            .build()
            );
            if (subQuestionDTO.getAnswers() != null && !subQuestionDTO.getAnswers().isEmpty()) {
                for (RequestAssignmentAnswersDTO answerDTO : subQuestionDTO.getAnswers()) {
                    System.out.println(answerDTO.getIsCorrect());
                    AssignmentQuestionAnswer selectedQuestionAnswer = AssignmentQuestionAnswer.builder()
                            .propertyId(UUID.randomUUID().toString())
                            .answer(answerDTO.getAnswer())
                            .isCorrect(answerDTO.getIsCorrect())
                            .assignmentSubQuestion(createdSubQuestion)
                            .build();

                    assignmentQuestionAswerRepository.save(selectedQuestionAnswer);
                }
            }
        }
    }

    private long generateAssignmentQuestionNumber(String assignmentId) {
        long last = assignmentQuestionRepository.getLastAssignmentQuestionNumber(assignmentId);
        return last + 1;
    }
    private long generateAssignmentSubQuestionNumber(String questionId) {
        long last = assignmentSubQuestionRepository.getLastSubQuestionNumber(questionId);
        return last + 1;
    }
}
