package com.lezord.system_api.service.impl;

import com.lezord.system_api.dto.request.RequestLessonAssignmentDTO;
import com.lezord.system_api.dto.response.ResponseLessonAssignmentDTO;
import com.lezord.system_api.entity.*;
import com.lezord.system_api.repository.*;
import com.nozomi.system_api.dto.response.*;
import com.lezord.system_api.dto.response.paginate.PaginatedLessonAssignmentDTO;
import com.nozomi.system_api.entity.*;
import com.lezord.system_api.entity.enums.LessonAssignmentStatusTypes;
import com.lezord.system_api.exception.DuplicateEntryException;
import com.lezord.system_api.exception.EntryNotFoundException;
import com.lezord.system_api.exception.InvalidAccessException;
import com.nozomi.system_api.repository.*;
import com.lezord.system_api.service.AssignmentQuestionTempService;
import com.lezord.system_api.service.LessonAssignmentService;
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
public class LessonAssignmentServiceImpl implements LessonAssignmentService {
    private final LessonAssignmentRepository lessonAssignmentRepo;
    private final CourseStageContentRepository courseStageContentRepository;
    private final IntakeRepository intakeRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AssignmentQuestionTempService assignmentQuestionTempService;
    private final LessonAssignmentTempRepository lessonAssignmentTempRepository;
    private final StudentHasAssignmentRepository studentHasAssignmentRepository;
    private final CourseRepository courseRepository;

    @Override
    public String createAssignment(RequestLessonAssignmentDTO dto, String lessonId, String intakeId) {
        if (dto == null) throw new EntryNotFoundException("Assignment Data Not Found");

        Optional<CourseStageContent> selectedLesson = courseStageContentRepository.findById(lessonId);
        if (selectedLesson.isEmpty()) throw new EntryNotFoundException("Lesson Data Not Found");

        Optional<Intake> selectedIntake = intakeRepository.findById(intakeId);
        if (selectedIntake.isEmpty()) throw new EntryNotFoundException("Intake data not found");

        LessonAssignment newAssignment = LessonAssignment.builder()
                .propertyId(UUID.randomUUID().toString())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .time(dto.getTime())
                .passValue(dto.getPassValue())
                .backwardAvailable(Boolean.TRUE.equals(dto.getBackwardAvailable()))
                .halfMarksForMultipleAnswers(Boolean.TRUE.equals(dto.getHalfMarksForMultipleAnswers()))
                .finalAssignment(Boolean.TRUE.equals(dto.getFinalAssignment()))
                .orderIndex(generateLessonAssignmentNumber(lessonId))
                .statusType(LessonAssignmentStatusTypes.UPCOMING)
                .lesson(selectedLesson.get())
                .intake(selectedIntake.get())
                .build();

        LessonAssignment savedAssignment = lessonAssignmentRepo.save(newAssignment);
        long assigmentCount = lessonAssignmentRepo.countLessonAssignmentsByLesson(selectedLesson.get());
        if (assigmentCount > selectedIntake.get().getCourse().getAssigmentCount()){
            Course selectedCourse = courseRepository.findById(selectedIntake.get().getPropertyId()).orElseThrow(() -> new EntryNotFoundException("Course Not Found"));
            selectedCourse.setAssigmentCount((int) assigmentCount);
            courseRepository.save(selectedCourse);
        }
        return savedAssignment.getPropertyId();
    }

    @Override
    public void updateAssignment(RequestLessonAssignmentDTO dto, String assignmentId) {
        if (dto == null) throw new EntryNotFoundException("Dto data not found");
        if (assignmentId.isEmpty()) throw new EntryNotFoundException("Assignment id not found");

        Optional<LessonAssignment> selectedAssignment = lessonAssignmentRepo.findById(assignmentId);
        if (selectedAssignment.isEmpty()) throw new EntryNotFoundException("Assignment Not Found");

        selectedAssignment.get().setTitle(dto.getTitle());
        selectedAssignment.get().setDescription(dto.getDescription());
        selectedAssignment.get().setTime(dto.getTime());
        selectedAssignment.get().setPassValue(dto.getPassValue());
        selectedAssignment.get().setBackwardAvailable(dto.getBackwardAvailable());
        selectedAssignment.get().setHalfMarksForMultipleAnswers(dto.getHalfMarksForMultipleAnswers());
        selectedAssignment.get().setFinalAssignment(dto.getFinalAssignment());

        lessonAssignmentRepo.save(selectedAssignment.get());
    }

    @Override
    @Transactional
    public void changeStatus(LessonAssignmentStatusTypes statusType, String assignmentId) {
        if (statusType == null) throw new EntryNotFoundException("StatusType Not Found");
        if (assignmentId.isEmpty()) throw new EntryNotFoundException("Assignment Id not found");

        Optional<LessonAssignment> selectedAssignment = lessonAssignmentRepo.findById(assignmentId);
        if (selectedAssignment.isEmpty()) throw new EntryNotFoundException("Assignment Data Not Found");

        LessonAssignment lessonAssignment = selectedAssignment.get();
        if (statusType == lessonAssignment.getStatusType())
            throw new DuplicateEntryException(String.format("Already Changed To %s", lessonAssignment.getStatusType()));
        lessonAssignment.setStatusType(statusType);

        lessonAssignmentRepo.save(lessonAssignment);
    }

    @Override
    public void deleteAssignment(String assignmentId) {
        if (assignmentId.isEmpty()) throw new EntryNotFoundException("Assignment Id Not Found");

        Optional<LessonAssignment> selectedAssignment = lessonAssignmentRepo.findById(assignmentId);
        if (selectedAssignment.isEmpty()) throw new EntryNotFoundException("Assignment Not Found");

        long orderIndex = selectedAssignment.get().getOrderIndex();
        CourseStageContent courseStageContent = selectedAssignment.get().getLesson();

        lessonAssignmentRepo.deleteById(assignmentId);

        List<LessonAssignment> updatedLessonAssignmentList = lessonAssignmentRepo.findByLessonAndOrderIndexGreaterThanOrderByOrderIndex(courseStageContent, Math.toIntExact(orderIndex)).stream().peek(lessonAssignment -> lessonAssignment.setOrderIndex(lessonAssignment.getOrderIndex() - 1)).toList();

        lessonAssignmentRepo.saveAll(updatedLessonAssignmentList);
    }

    @Override
    public ResponseLessonAssignmentDTO findById(String assignmentId) {
        if (assignmentId.isEmpty()) throw new EntryNotFoundException("Assignment id not found");

        Optional<LessonAssignment> selectedAssignment = lessonAssignmentRepo.findById(assignmentId);
        if (selectedAssignment.isEmpty()) throw new EntryNotFoundException("Assignment data not found");

        return ResponseLessonAssignmentDTO.builder()
                .propertyId(selectedAssignment.get().getPropertyId())
                .title(selectedAssignment.get().getTitle())
                .description(selectedAssignment.get().getDescription())
                .time(selectedAssignment.get().getTime())
                .passValue(selectedAssignment.get().getPassValue())
                .backwardAvailable(selectedAssignment.get().getBackwardAvailable())
                .halfMarksForMultipleAnswers(selectedAssignment.get().getHalfMarksForMultipleAnswers())
                .orderIndex(selectedAssignment.get().getOrderIndex())
                .finalAssignment(selectedAssignment.get().getFinalAssignment())
                .statusType(selectedAssignment.get().getStatusType())
                .build();
    }

    @Override
    public String getIntakeIdByCourseAndStudentIds(String courseId, String studentId) {
        if (courseId.isEmpty()) throw new EntryNotFoundException("course if not found");
        if (studentId.isEmpty()) throw new EntryNotFoundException("Student id not found");

        Optional<Enrollment> selectedData = enrollmentRepository.findByStudentPropertyIdAndCoursePropertyId(studentId, courseId);
        if (selectedData.isEmpty()) throw new EntryNotFoundException("Enrollment data not found");

        return selectedData.get().getIntake().getPropertyId();
    }

    @Override
    public PaginatedLessonAssignmentDTO getAllByLessonIdAndIntake(String lessonId, String intake, String studentId, int page, int size, Boolean areOnlyActivated) {
        if (studentId != null && !studentId.isBlank()) {
            Optional<Enrollment> selectedEnrollment = enrollmentRepository.getEnrollmentByStudentAndIntake(studentId, intake);

            if (selectedEnrollment.isPresent()) {
                Enrollment enrollment = selectedEnrollment.get();
                if (!enrollment.getCanAccessCourse()) {
                    throw new InvalidAccessException("Please pay your installment before making assignments.");
                }
            }
        }

        List<LessonAssignmentStatusTypes> types = List.of(
                LessonAssignmentStatusTypes.ACTIVATED,
                LessonAssignmentStatusTypes.CLOSED
        );

        if (page == -1 && size == -1) {
            List<LessonAssignment> allAssignmentsList = null;

            if (!areOnlyActivated) {
                allAssignmentsList = lessonAssignmentRepo.findByLessonPropertyIdAndIntakePropertyIdOrderByOrderIndexAsc(lessonId, intake);
            } else {
                allAssignmentsList = lessonAssignmentRepo.findByLessonPropertyIdAndIntakePropertyIdAndStatusTypeInOrderByOrderIndexAsc(lessonId, intake, types);
            }

            return PaginatedLessonAssignmentDTO.builder()
                    .count(lessonAssignmentRepo.findAllCount(lessonId))
                    .dataList(
                            convertToLessonAssignmentList(allAssignmentsList)
                    )
                    .build();
        } else {
            Page<LessonAssignment> allAssignments = null;

            Pageable pageable = PageRequest.of(page, size);

            if (!areOnlyActivated) {
                allAssignments = lessonAssignmentRepo.findByLessonPropertyIdAndIntakePropertyIdOrderByOrderIndexAsc(lessonId, intake, pageable);
            } else {
                allAssignments = lessonAssignmentRepo.findByLessonPropertyIdAndIntakePropertyIdAndStatusTypeInOrderByOrderIndexAsc(lessonId, intake, types, pageable);
            }

            return PaginatedLessonAssignmentDTO.builder()
                    .count(lessonAssignmentRepo.findAllCount(lessonId))
                    .dataList(
                            convertToLessonAssignment(allAssignments)
                    )
                    .build();
        }
    }

    private List<ResponseLessonAssignmentDTO> convertToLessonAssignment(Page<LessonAssignment> allAssignments) {
        List<ResponseLessonAssignmentDTO> finalList = new ArrayList<>();
        for (LessonAssignment l : allAssignments) {
            ResponseLessonAssignmentDTO assignment = ResponseLessonAssignmentDTO.builder()
                    .propertyId(l.getPropertyId())
                    .title(l.getTitle())
                    .description(l.getDescription())
                    .time(l.getTime())
                    .passValue(l.getPassValue())
                    .backwardAvailable(l.getBackwardAvailable())
                    .halfMarksForMultipleAnswers(l.getHalfMarksForMultipleAnswers())
                    .orderIndex(l.getOrderIndex())
                    .finalAssignment(l.getFinalAssignment())
                    .statusType(l.getStatusType())
                    .build();

            finalList.add(assignment);
        }
        return finalList;
    }

    private List<ResponseLessonAssignmentDTO> convertToLessonAssignmentList(List<LessonAssignment> allAssignments) {
        List<ResponseLessonAssignmentDTO> finalList = new ArrayList<>();
        for (LessonAssignment l : allAssignments) {
            ResponseLessonAssignmentDTO assignment = ResponseLessonAssignmentDTO.builder()
                    .propertyId(l.getPropertyId())
                    .title(l.getTitle())
                    .description(l.getDescription())
                    .time(l.getTime())
                    .passValue(l.getPassValue())
                    .backwardAvailable(l.getBackwardAvailable())
                    .halfMarksForMultipleAnswers(l.getHalfMarksForMultipleAnswers())
                    .orderIndex(l.getOrderIndex())
                    .finalAssignment(l.getFinalAssignment())
                    .statusType(l.getStatusType())
                    .build();

            finalList.add(assignment);
        }
        return finalList;
    }

    private int generateLessonAssignmentNumber(String lessonId) {
        int last = lessonAssignmentRepo.getLastLessonAssignmentNumber(lessonId);
        return last + 1;
    }

}
