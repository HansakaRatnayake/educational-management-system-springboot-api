package com.lezord.system_api.service.impl;

import com.lezord.system_api.dto.request.RequestStudentHasAssignmentDTO;
import com.lezord.system_api.dto.request.RequestStudentHasAssignmentUpdateDTO;
import com.lezord.system_api.dto.response.ResponseLessonAssignmentDTO;
import com.lezord.system_api.dto.response.ResponseStudentHasAssignmentCustomDTO;
import com.lezord.system_api.dto.response.ResponseStudentHasAssignmentDTO;
import com.lezord.system_api.dto.response.paginate.PaginatedStudentHasAssignmentDTO;
import com.lezord.system_api.entity.*;
import com.lezord.system_api.repository.*;
import com.nozomi.system_api.entity.*;
import com.lezord.system_api.entity.core.StudentAssignmentId;
import com.lezord.system_api.entity.enums.LessonAssignmentStatusTypes;
import com.lezord.system_api.entity.enums.StudentHasAssignmentMarksTypes;
import com.lezord.system_api.entity.enums.StudentHasAssignmentTypes;
import com.lezord.system_api.exception.DuplicateEntryException;
import com.lezord.system_api.exception.EntryNotFoundException;
import com.lezord.system_api.exception.InvalidAccessException;
import com.nozomi.system_api.repository.*;
import com.lezord.system_api.service.StudentHasAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentHasAssignmentServiceImpl implements StudentHasAssignmentService {
    private final StudentHasAssignmentRepository studentHasAssignmentRepository;
    private final StudentHasAssignmentStatusTrackerRepository studentHasAssignmentStatusTrackerRepository;
    private final StudentRepository studentRepository;
    private final LessonAssignmentRepository lessonAssignmentRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public String createStudentAssignment(RequestStudentHasAssignmentDTO dto) {
        if (dto == null) throw new EntryNotFoundException("Dto data not found");

        Optional<Student> selectedStudent = studentRepository.findById(dto.getStudentId());
        if (selectedStudent.isEmpty())
            throw new EntryNotFoundException("Student data not found and please check your student id");

        Optional<LessonAssignment> selectedAssignment = lessonAssignmentRepository.findById(dto.getAssignmentId());
        if (selectedAssignment.isEmpty())
            throw new EntryNotFoundException("Assignment data not found and please check your assignment id");

        if (selectedAssignment.get().getStatusType() != LessonAssignmentStatusTypes.ACTIVATED)
            throw new InvalidAccessException(String.format("This %s Has Been Closed By The Panel And Must Wait Until This Assignment Is Reactivated.",selectedAssignment.get().getTitle()));

        Optional<StudentHasAssignmentStatusTracker> selectedStudentAssignmentTracker = studentHasAssignmentStatusTrackerRepository.findByStudentAssignmentIdStudentIdAndStudentAssignmentIdAssignmentId(dto.getStudentId(), dto.getAssignmentId());

        String studentAssignmentId = null;

        if (selectedStudentAssignmentTracker.isEmpty() || selectedStudentAssignmentTracker.get().getIsRepeatedAccepted().equals(Boolean.TRUE)) {
            StudentHasAssignment savedStudentHasAssignment = StudentHasAssignment.builder()
                    .propertyId(UUID.randomUUID().toString())
                    .student(selectedStudent.get())
                    .assignment(selectedAssignment.get())
                    .createdAt(Instant.now())
                    .fullMarks(null)
                    .passValue(selectedAssignment.get().getPassValue())
                    .marksType(StudentHasAssignmentMarksTypes.PENDING)
                    .statusType(StudentHasAssignmentTypes.INCOMPLETED)
                    .build();

            studentHasAssignmentRepository.save(savedStudentHasAssignment);
            studentAssignmentId = savedStudentHasAssignment.getPropertyId();
        } else {
            throw new DuplicateEntryException("You cannot do this assignment again. Because you already did this assignment. " +
                    "If you need to do this assignment again, you should request first");
        }

        if (selectedStudentAssignmentTracker.isPresent()) {
            if (selectedStudentAssignmentTracker.get().getIsRepeatedAccepted()) {
                selectedStudentAssignmentTracker.get().setIsRepeatedAccepted(false);
                studentHasAssignmentStatusTrackerRepository.save(selectedStudentAssignmentTracker.get());
            }
        } else {
            studentHasAssignmentStatusTrackerRepository.save(
                    StudentHasAssignmentStatusTracker.builder()
                            .studentAssignmentId(StudentAssignmentId.builder()
                                    .studentId(dto.getStudentId())
                                    .assignmentId(dto.getAssignmentId())
                                    .build())
                            .isRepeatedAccepted(false)
                            .attemps(1L)
                            .build()
            );
        }

        return studentAssignmentId;
    }

    @Override
    public void updateStudentAssignment(RequestStudentHasAssignmentUpdateDTO dto, String studentHasAssignmentId, StudentHasAssignmentTypes type) {
        if (dto == null) throw new EntryNotFoundException("Dto data not found");
        if (studentHasAssignmentId.isEmpty()) throw new EntryNotFoundException("Student has assignment id not found");

        Optional<StudentHasAssignment> selectedStudentHasAssignment = studentHasAssignmentRepository.findById(studentHasAssignmentId);
        if (selectedStudentHasAssignment.isEmpty())
            throw new EntryNotFoundException("Student has assignment data not found and please check your student has assignment id");

        selectedStudentHasAssignment.get().setFullMarks(dto.getFullMarks());
        selectedStudentHasAssignment.get().setUpdateAt(Instant.now());
        studentHasAssignmentRepository.save(selectedStudentHasAssignment.get());

        changeMarksType(studentHasAssignmentId, dto.getMarksType());
        changeStatus(studentHasAssignmentId,type);
    }

    @Override
    public ResponseStudentHasAssignmentDTO findByStudentAndAssignmentIds(String studentId, String assignmentId) {
        if (studentId.isEmpty()) throw new EntryNotFoundException("Student id not found");
        if (assignmentId.isEmpty()) throw new EntryNotFoundException("Assignment id not found");

        Optional<StudentHasAssignmentStatusTracker> selectedTrackerStudentAssignment
                = studentHasAssignmentStatusTrackerRepository.findByStudentAssignmentIdStudentIdAndStudentAssignmentIdAssignmentId(studentId, assignmentId);

        Optional<StudentHasAssignment> selectedStudentAssignment
                = studentHasAssignmentRepository.findFirstByStudentPropertyIdAndAssignmentPropertyIdOrderByCreatedAtDesc(studentId, assignmentId);

        if (selectedStudentAssignment.isEmpty())
            return null;

        return ResponseStudentHasAssignmentDTO.builder()
                .propertyId(selectedStudentAssignment.get().getPropertyId())
                .marksType(selectedStudentAssignment.get().getMarksType())
                .fullMarks(selectedStudentAssignment.get().getFullMarks())
                .passValue(selectedStudentAssignment.get().getPassValue())
                .updateAt(selectedStudentAssignment.get().getUpdateAt())
                .displayStatus(
                        selectedTrackerStudentAssignment.get().getIsRepeatedAccepted() ? StudentHasAssignmentTypes.REPEAT_START : selectedStudentAssignment.get().getStatusType()
                )
                .build();

    }

    @Override
    public PaginatedStudentHasAssignmentDTO getAllCompletedAssignmentWithStudentMarks(String studentId, String intakeId, String contentTypeId, int page, int size) {
        if (studentId.isEmpty()) throw new EntryNotFoundException("Student id not found");

        Pageable pageable = PageRequest.of(page, size);

        List<StudentHasAssignmentTypes> types = List.of(StudentHasAssignmentTypes.COMPLETED, StudentHasAssignmentTypes.CLOSED);

        String intakeFinalId = null;

        if (intakeId == null || intakeId.isBlank() || intakeId.isEmpty()){
            Optional<Enrollment> enrollmentData = enrollmentRepository.findFirstByStudentPropertyIdOrderByCreatedDateDesc(studentId);
            if (enrollmentData.isPresent()) intakeFinalId = enrollmentData.get().getIntake().getPropertyId();
        }else {
            intakeFinalId = intakeId;
        }

        Page<StudentHasAssignment> allData = studentHasAssignmentRepository.findAllCompletedAssignments(studentId, intakeFinalId, contentTypeId, types,pageable);

        return PaginatedStudentHasAssignmentDTO.builder()
                .count(allData.getTotalElements())
                .dataList(convertToStudentHasAssignmentDTOs(allData))
                .build();
    }

    private List<ResponseStudentHasAssignmentCustomDTO> convertToStudentHasAssignmentDTOs(Page<StudentHasAssignment> allData) {
        List<ResponseStudentHasAssignmentCustomDTO> list = new ArrayList<>();
        allData.forEach(item -> {
            list.add(
                    ResponseStudentHasAssignmentCustomDTO.builder()
                            .propertyId(item.getPropertyId())
                            .assignment(convertToAssignmentDTO(item.getAssignment()))
                            .studentId(item.getStudent().getPropertyId())
                            .createdAt(item.getCreatedAt())
                            .updateAt(item.getUpdateAt())
                            .passValue(item.getPassValue())
                            .marksType(item.getMarksType())
                            .statusType(item.getStatusType())
                            .fullMarks(item.getFullMarks())
                            .build()
            );
        });
        return list;
    }

    private ResponseLessonAssignmentDTO convertToAssignmentDTO(LessonAssignment assignment) {
        return ResponseLessonAssignmentDTO.builder()
                .propertyId(assignment.getPropertyId())
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .time(assignment.getTime())
                .passValue(assignment.getPassValue())
                .backwardAvailable(assignment.getBackwardAvailable())
                .halfMarksForMultipleAnswers(assignment.getHalfMarksForMultipleAnswers())
                .orderIndex(assignment.getOrderIndex())
                .finalAssignment(assignment.getFinalAssignment())
                .statusType(assignment.getStatusType())
                .build();
    }

    private void changeStatus(String studentHasAssignmentId , StudentHasAssignmentTypes type) {
        System.out.println(type);
        if (studentHasAssignmentId.isEmpty()) throw new EntryNotFoundException("Student has assignment id not found");

        Optional<StudentHasAssignment> selectedStudentHasAssignment = studentHasAssignmentRepository.findById(studentHasAssignmentId);
        if (selectedStudentHasAssignment.isEmpty())
            throw new EntryNotFoundException("Student has assignment data not found and please check your student has assignment id");


        selectedStudentHasAssignment.get().setStatusType(type);
        studentHasAssignmentRepository.save(selectedStudentHasAssignment.get());
    }

    private void changeMarksType(String studentHasAssignmentId, StudentHasAssignmentMarksTypes marksType) {
        if (studentHasAssignmentId.isEmpty()) throw new EntryNotFoundException("Student has assignment id not found");

        Optional<StudentHasAssignment> selectedStudentHasAssignment = studentHasAssignmentRepository.findById(studentHasAssignmentId);
        if (selectedStudentHasAssignment.isEmpty())
            throw new EntryNotFoundException("Student has assignment data not found and please check your student has assignment id");


        selectedStudentHasAssignment.get().setMarksType(marksType);
        studentHasAssignmentRepository.save(selectedStudentHasAssignment.get());
        setCourseCompleteness(selectedStudentHasAssignment.get(), marksType);
    }

    private void setCourseCompleteness(StudentHasAssignment studentHasAssignment, StudentHasAssignmentMarksTypes marksType) {
        if (studentHasAssignment.getAssignment().getFinalAssignment() && marksType == StudentHasAssignmentMarksTypes.PASSED){
            Enrollment enrollment = enrollmentRepository.findByStudentPropertyIdAndCoursePropertyId(
                    studentHasAssignment.getStudent().getPropertyId(), studentHasAssignment.getAssignment().getIntake().getCourse().getPropertyId()
                    ).orElseThrow(() -> new EntryNotFoundException("Enrollment not found for this assigment"));
            enrollment.setCourseCompleteness(true);
            enrollmentRepository.save(enrollment);


        }
    }
}
