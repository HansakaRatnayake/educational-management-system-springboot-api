package com.lezord.system_api.service.impl;

import com.lezord.system_api.dto.request.RequestAssignmentAnswersDTO;
import com.lezord.system_api.dto.request.RequestAssignmentSubQuestionDTO;
import com.lezord.system_api.dto.request.RequestLessonAssignmentTempDTO;
import com.lezord.system_api.dto.response.*;
import com.lezord.system_api.entity.*;
import com.lezord.system_api.repository.*;
import com.nozomi.system_api.dto.response.*;
import com.lezord.system_api.dto.response.paginate.PaginatedAssignmentQuestionTempOrderIndexDTO;
import com.nozomi.system_api.entity.*;
import com.lezord.system_api.entity.enums.LessonAssignmentStatusTypes;
import com.lezord.system_api.entity.enums.LessonAssignmentTempDidTypes;
import com.lezord.system_api.exception.EntryNotFoundException;
import com.lezord.system_api.exception.InvalidAccessException;
import com.nozomi.system_api.repository.*;
import com.lezord.system_api.service.AssignmentQuestionTempService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class AssignmentQuestionTempServiceImpl implements AssignmentQuestionTempService {
    private final AssignmentQuestionTempRepository assignmentQuestionTempRepository;
    private final StudentRepository studentRepository;
    private final LessonAssignmentRepository lessonAssignmentRepository;
    private final AssignmentSubQuestionTempRepository assignmentSubQuestionTempRepository;
    private final AssignmentQuestionAnswerTempRepository assignmentQuestionAnswerTempRepository;
    private final LessonAssignmentTempRepository lessonAssignmentTempRepository;

    @Override
    public void createQuestionsTemp(RequestLessonAssignmentTempDTO dto, String studentId, String assignmentId) {
        if (dto == null) throw new EntryNotFoundException("Requested data not found");
        if (studentId.isEmpty()) throw new EntryNotFoundException("Student id not found");
        if (assignmentId.isEmpty()) throw new EntryNotFoundException("Assignment id not found");

        Student selectedStudent = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntryNotFoundException("Student data not found"));

        LessonAssignment selectedAssignment = lessonAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntryNotFoundException("Assignment id not found"));

        LessonAssignmentTemp savedTempAssignment = lessonAssignmentTempRepository.save(
                LessonAssignmentTemp.builder()
                        .propertyId(UUID.randomUUID().toString())
                        .time((double) selectedAssignment.getTime())
                        .currentTime((double) selectedAssignment.getTime())
                        .currentIndex(1L)
                        .backwardAvailable(dto.getBackwardAvailable())
                        .halfMarksForMultipleAnswers(dto.getHalfMarksForMultipleAnswers())
                        .finalAssignment(dto.getFinalAssignment())
                        .createdAt(Instant.now())
                        .student(selectedStudent)
                        .lessonAssignment(selectedAssignment)
                        .build()
        );

        dto.getAssignmentQuestionTemps().forEach(item -> {
            AssignmentQuestionTemp saveData = assignmentQuestionTempRepository.save(
                    AssignmentQuestionTemp.builder()
                            .propertyId(UUID.randomUUID().toString())
                            .assignmentQuestionImage(item.getAssignmentQuestionImage())
                            .assignmentQuestionRecording(item.getAssignmentQuestionRecording())
                            .paragraph(item.getParagraph())
                            .orderIndex(item.getOrderIndex())
                            .isBookmark(Boolean.FALSE)
                            .marks(0.00)
                            .filled(item.getOrderIndex() == 1 ? LessonAssignmentTempDidTypes.CURRENT : LessonAssignmentTempDidTypes.NOT_FILLED)
                            .lessonAssignmentTemp(savedTempAssignment)
                            .build()
            );
            saveAssignmentSubQuestionTemps(item.getAssignmentSubQuestionTemps(), saveData);
        });
    }

    @Override
    public ResponseLessonAssignmentTempDTO getAllAssignmentData(String studentId, String assignmentId) {
        if (studentId.isEmpty()) throw new EntryNotFoundException("Student id not found");
        if (assignmentId.isEmpty()) throw new EntryNotFoundException("Assignment id not found");

        Optional<LessonAssignmentTemp> selectedAllData
                = lessonAssignmentTempRepository.findFirstByStudentPropertyIdAndLessonAssignmentPropertyIdOrderByCreatedAtDesc(studentId, assignmentId);

        if (selectedAllData.isEmpty())
            return ResponseLessonAssignmentTempDTO.builder().build();

        return ResponseLessonAssignmentTempDTO.builder()
                .propertyId(selectedAllData.get().getPropertyId())
                .time(selectedAllData.get().getTime())
                .currentTime(selectedAllData.get().getCurrentTime())
                .currentIndex(selectedAllData.get().getCurrentIndex())
                .backwardAvailable(selectedAllData.get().getBackwardAvailable())
                .halfMarksForMultipleAnswers(selectedAllData.get().getHalfMarksForMultipleAnswers())
                .finalAssignment(selectedAllData.get().getFinalAssignment())
                .build();
    }

    @Override
    public ResponseAssignmentQuestionTempDTO getQuestionsOneByOne(String assignmentTempId, int index) {
        if (assignmentTempId.isEmpty()) throw new EntryNotFoundException("Assignment temp id not found");
        if (index <= 0) throw new EntryNotFoundException("Index must not be less that 1");

        Optional<AssignmentQuestionTemp> allData
                = assignmentQuestionTempRepository.findAllByLessonAssignmentTempPropertyIdAndOrderIndex(assignmentTempId, index);
        if (allData.isEmpty())
            return ResponseAssignmentQuestionTempDTO.builder().build();

        Optional<LessonAssignmentTemp> tempLessonAssignmentData
                = lessonAssignmentTempRepository.findById(allData.get().getLessonAssignmentTemp().getPropertyId());
        if (tempLessonAssignmentData.isEmpty())
            throw new EntryNotFoundException("Temp assignment not found");

        Optional<LessonAssignment> selectedAssignment = lessonAssignmentRepository.findById(tempLessonAssignmentData.get().getLessonAssignment().getPropertyId());

        if (selectedAssignment.get().getStatusType() != LessonAssignmentStatusTypes.ACTIVATED)
            throw new InvalidAccessException(String.format("This %s Has Been Closed By The Panel And Must Wait Until This Assignment Is Reactivated.",selectedAssignment.get().getTitle()));

        return ResponseAssignmentQuestionTempDTO.builder()
                .propertyId(allData.get().getPropertyId())
                .assignmentQuestionImage(allData.get().getAssignmentQuestionImage())
                .assignmentQuestionRecording(allData.get().getAssignmentQuestionRecording())
                .paragraph(allData.get().getParagraph())
                .orderIndex(allData.get().getOrderIndex())
                .marks(allData.get().getMarks())
                .isBookmark(allData.get().getIsBookmark())
                .filled(allData.get().getFilled())
                .assignmentSubQuestionTemps(convertToSubAssignmentQuestionTemp(allData.get().getAssignmentSubQuestionTemps()))
                .build();
    }

    @Override
    public PaginatedAssignmentQuestionTempOrderIndexDTO getQuestionCount(String assignmentTempId) {
        if (assignmentTempId.isEmpty()) throw new EntryNotFoundException("Assignment id not found");

        List<AssignmentQuestionTemp> allData = assignmentQuestionTempRepository.findAllByLessonAssignmentTempPropertyIdOrderByOrderIndexAsc(assignmentTempId);
        if (allData.isEmpty()) return PaginatedAssignmentQuestionTempOrderIndexDTO.builder().build();

        return PaginatedAssignmentQuestionTempOrderIndexDTO.builder()
                .count((long) allData.size())
                .dataList(convertToAssignmentQuestionTempOrderIndex(allData))
                .build();

    }

    private List<ResponseAssignmentQuestionTempOrderIndexDTO> convertToAssignmentQuestionTempOrderIndex(List<AssignmentQuestionTemp> allData) {
        List<ResponseAssignmentQuestionTempOrderIndexDTO> list = new ArrayList<>();
        allData.forEach(item -> {
            list.add(
                    ResponseAssignmentQuestionTempOrderIndexDTO.builder()
                            .propertyId(item.getPropertyId())
                            .orderIndex(Math.toIntExact(item.getOrderIndex()))
                            .isBookmark(item.getIsBookmark())
                            .filled(item.getFilled())
                            .build()
            );
        });
        return list;
    }

    @Override
    @Transactional
    public void changeCurrentTime(String assignmentTempId, Double currentTime) {
        if (assignmentTempId.isEmpty()) throw new EntryNotFoundException("Assignment temp id not found");

        Optional<LessonAssignmentTemp> selectedAssignment = lessonAssignmentTempRepository.findById(assignmentTempId);
        if (selectedAssignment.isEmpty()) throw new EntryNotFoundException("Temp Lesson Assignment not found");

        selectedAssignment.get().setCurrentTime(currentTime);
        lessonAssignmentTempRepository.save(selectedAssignment.get());
    }

    @Override
    @Transactional
    public void changeCurrentIndex(String assignmentTempId, int currentIndex) {
        if (assignmentTempId.isEmpty()) throw new EntryNotFoundException("Assignment id not found");

        Optional<LessonAssignmentTemp> selectedData = lessonAssignmentTempRepository.findById(assignmentTempId);
        if (selectedData.isEmpty()) throw new EntryNotFoundException("Temp assignment not found");

        selectedData.get().setCurrentIndex((long) currentIndex);
        lessonAssignmentTempRepository.save(selectedData.get());
    }

    @Override
    public int getCurrentIndex(String assignmentTempId) {
        if (assignmentTempId.isEmpty()) throw new EntryNotFoundException("Assignment temp id not found");

        Optional<AssignmentQuestionTemp> selectedData = assignmentQuestionTempRepository.findByLessonAssignmentTempPropertyIdAndFilled(assignmentTempId, LessonAssignmentTempDidTypes.CURRENT);
        if (selectedData.isEmpty()) throw new EntryNotFoundException("Assignment current question data not found");

        return Math.toIntExact(selectedData.get().getOrderIndex());
    }

    @Override
    public AtomicReference<Double> getAllAssignmentQuestionMarks(String assignmentTempId) {
        if (assignmentTempId.isEmpty()) throw new EntryNotFoundException("Assignment temp id not found");

        List<AssignmentQuestionTemp> allData = assignmentQuestionTempRepository.findAllByLessonAssignmentTempPropertyId(assignmentTempId);

        AtomicReference<Double> marksCount = new AtomicReference<>(0.00);
        allData.forEach(item -> {
            marksCount.updateAndGet(v -> v + item.getMarks());
        });
        return marksCount;
    }

    @Override
    @Transactional
    public void changeFilledStatus(String assignmentTempId, int orderIndex, LessonAssignmentTempDidTypes type) {
        if (assignmentTempId.isEmpty()) throw new EntryNotFoundException("Assignment temp id not found");
        if (orderIndex <= 0) throw new EntryNotFoundException("Order index must be greater than 0");

        Optional<AssignmentQuestionTemp> selectedData = assignmentQuestionTempRepository.findByLessonAssignmentTempPropertyIdAndOrderIndex(assignmentTempId, orderIndex);
        if (selectedData.isEmpty()) throw new EntryNotFoundException("Question data not found");

        selectedData.get().setFilled(type);

        assignmentQuestionTempRepository.save(selectedData.get());
    }

    @Override
    @Transactional
    public void changeAnswerSelectState(String answerId) {
        if (answerId.isEmpty()) throw new EntryNotFoundException("Answer id not found");

        Optional<AssignmentQuestionAnswerTemp> selectedAnswer = assignmentQuestionAnswerTempRepository.findById(answerId);
        if (selectedAnswer.isEmpty()) throw new EntryNotFoundException("Answer data not found");

        selectedAnswer.get().setIsStudentSelect(!selectedAnswer.get().getIsStudentSelect());
        assignmentQuestionAnswerTempRepository.save(selectedAnswer.get());
    }

    @Override
    @Transactional
    public void changeAssignmentQuestionMarks(String questionId, Double marks) {
        if (questionId.isEmpty()) throw new EntryNotFoundException("Question id not found");

        Optional<AssignmentQuestionTemp> selectedData = assignmentQuestionTempRepository.findById(questionId);
        if (selectedData.isEmpty()) throw new EntryNotFoundException("Assignment temp question data not found");

        selectedData.get().setMarks(marks);

        assignmentQuestionTempRepository.save(selectedData.get());
    }

    @Override
    public void deleteTempLessonAssignment(String assignmentTempId) {
        if (assignmentTempId.isEmpty()) throw new EntryNotFoundException("Assignment temp id not found");

        Optional<LessonAssignmentTemp> selectedAssignment = lessonAssignmentTempRepository.findById(assignmentTempId);
        if (selectedAssignment.isEmpty()) throw new EntryNotFoundException("Temp assignment data not found");

        deleteTempQuestion(selectedAssignment.get().getAssignmentQuestionTemps());
        lessonAssignmentTempRepository.deleteById(selectedAssignment.get().getPropertyId());
    }

    @Override
    public void changeBookmarkStatus(String assignmentTempQuestionId) {
        if (assignmentTempQuestionId.isEmpty())
            throw new EntryNotFoundException("Assignment temp question id not found");

        Optional<AssignmentQuestionTemp> selectedData = assignmentQuestionTempRepository.findById(assignmentTempQuestionId);
        if (selectedData.isEmpty()) throw new EntryNotFoundException("Assignment temp question not found");

        selectedData.get().setIsBookmark(!selectedData.get().getIsBookmark());
        assignmentQuestionTempRepository.save(selectedData.get());
    }

    private void deleteTempQuestion(List<AssignmentQuestionTemp> assignmentQuestionTemps) {
        assignmentQuestionTemps.forEach(item -> {
            deleteTempSubQuestions(item.getAssignmentSubQuestionTemps());
            assignmentQuestionTempRepository.deleteById(item.getPropertyId());
        });
    }

    private void deleteTempSubQuestions(List<AssignmentSubQuestionTemp> assignmentSubQuestionTemps) {
        assignmentSubQuestionTemps.forEach(item -> {
            deleteAnswers(item.getAssignmentQuestionAnswerTemps());
            assignmentSubQuestionTempRepository.deleteById(item.getPropertyId());
        });
    }

    private void deleteAnswers(List<AssignmentQuestionAnswerTemp> assignmentQuestionAnswerTemps) {
        assignmentQuestionAnswerTemps.forEach(item -> {
            assignmentQuestionAnswerTempRepository.deleteById(item.getPropertyId());
        });
    }

    private List<ResponseAssignmentSubQuestionTempDTO> convertToSubAssignmentQuestionTemp(List<AssignmentSubQuestionTemp> assignmentSubQuestionTemps) {
        List<ResponseAssignmentSubQuestionTempDTO> list = new ArrayList<>();
        assignmentSubQuestionTemps.forEach(item -> {
            list.add(
                    ResponseAssignmentSubQuestionTempDTO.builder()
                            .propertyId(item.getPropertyId())
                            .question(item.getQuestion())
                            .orderIndex(item.getOrderIndex())
                            .assignmentQuestionAnswerTemps(convertToAnswerTemp(item.getAssignmentQuestionAnswerTemps()))
                            .build()
            );
        });
        return list;
    }

    private List<ResponseAssignmentQuestionAnswerTempDTO> convertToAnswerTemp(List<AssignmentQuestionAnswerTemp> assignmentQuestionAnswerTemps) {
        List<ResponseAssignmentQuestionAnswerTempDTO> list = new ArrayList<>();
        assignmentQuestionAnswerTemps.forEach(item -> {
            list.add(
                    ResponseAssignmentQuestionAnswerTempDTO.builder()
                            .propertyId(item.getPropertyId())
                            .answer(item.getAnswer())
                            .isCorrect(item.isCorrect())
                            .isStudentSelect(item.getIsStudentSelect())
                            .build()
            );
        });
        return list;
    }

    private void saveAssignmentSubQuestionTemps(List<RequestAssignmentSubQuestionDTO> assignmentSubQuestionTemps, AssignmentQuestionTemp saveData) {
        assignmentSubQuestionTemps.forEach(item -> {
            AssignmentSubQuestionTemp selected = assignmentSubQuestionTempRepository.save(
                    AssignmentSubQuestionTemp.builder()
                            .propertyId(UUID.randomUUID().toString())
                            .question(item.getQuestion())
                            .orderIndex(item.getOrderIndex())
                            .assignmentQuestionTemp(saveData)
                            .build()
            );
            saveAssignmentAnswerTemp(item.getAnswers(), selected);
        });
    }

    private void saveAssignmentAnswerTemp(List<RequestAssignmentAnswersDTO> answers, AssignmentSubQuestionTemp selected) {
        List<AssignmentQuestionAnswerTemp> list = new ArrayList<>();
        answers.forEach(item -> {
            AssignmentQuestionAnswerTemp selectedAnswers = AssignmentQuestionAnswerTemp.builder()
                    .propertyId(UUID.randomUUID().toString())
                    .answer(item.getAnswer())
                    .isCorrect(item.getIsCorrect())
                    .isStudentSelect(item.getIsStudentSelect())
                    .assignmentSubQuestionTemp(selected)
                    .build();
            list.add(selectedAnswers);
        });
        assignmentQuestionAnswerTempRepository.saveAll(list);
    }
}
