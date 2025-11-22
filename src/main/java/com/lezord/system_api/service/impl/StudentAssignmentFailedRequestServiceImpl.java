package com.lezord.system_api.service.impl;

import com.lezord.system_api.dto.response.ResponseFailedRequestCustomDTO;
import com.lezord.system_api.dto.response.ResponseStudentAssignmentFailedRequestDTO;
import com.lezord.system_api.dto.response.paginate.PaginatedFailedRequestsDTO;
import com.lezord.system_api.entity.StudentAssignmentFailedRequest;
import com.lezord.system_api.entity.StudentHasAssignment;
import com.lezord.system_api.entity.StudentHasAssignmentStatusTracker;
import com.lezord.system_api.repository.*;
import com.nozomi.system_api.entity.*;
import com.lezord.system_api.entity.enums.StudentHasAssignmentMarksTypes;
import com.lezord.system_api.exception.DuplicateEntryException;
import com.lezord.system_api.exception.EntryNotFoundException;
import com.lezord.system_api.exception.InvalidAccessException;
import com.nozomi.system_api.repository.*;
import com.lezord.system_api.service.StudentAssignmentFailedRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StudentAssignmentFailedRequestServiceImpl implements StudentAssignmentFailedRequestService {
    private final StudentAssignmentFailedRequestRepository studentAssignmentFailedRequestRepository;
    private final StudentHasAssignmentRepository studentHasAssignmentRepository;
    private final StudentHasAssignmentStatusTrackerRepository studentHasAssignmentStatusTrackerRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final LessonAssignmentRepository lessonAssignmentRepository;

    @Override
    public void createRequest(String studentId, String assignmentId) {
        if (studentId.isEmpty()) throw new EntryNotFoundException("Student id not found");
        if (assignmentId.isEmpty()) throw new EntryNotFoundException("Assignment id not found");

        Optional<StudentHasAssignmentStatusTracker> selectedTracker = studentHasAssignmentStatusTrackerRepository.findByStudentAssignmentIdStudentIdAndStudentAssignmentIdAssignmentId(studentId, assignmentId);
        if (selectedTracker.isEmpty())
            throw new EntryNotFoundException("Student assignment tracker not found or you did not do this assignment yet");

        if (selectedTracker.get().getIsRepeatedAccepted())
            throw new EntryNotFoundException("You are eligible to repeat this assignment, so do it first. In this time you cannot request for another repeat");

        Optional<StudentHasAssignment> selectedStudentAssignment = studentHasAssignmentRepository.findFirstByStudentPropertyIdAndAssignmentPropertyIdOrderByCreatedAtDesc(studentId, assignmentId);
        if (selectedStudentAssignment.isEmpty()) throw new EntryNotFoundException("You did not do this assignment yet");
        if (!selectedStudentAssignment.get().getMarksType().equals(StudentHasAssignmentMarksTypes.FAILED))
            throw new InvalidAccessException("You Did not failed this exam, so you cannot request for repeat");

        Optional<StudentAssignmentFailedRequest> selectedStudentAssignmentFailed = studentAssignmentFailedRequestRepository.findFirstByStudentPropertyIdAndLessonAssignmentPropertyIdOrderByCreatedAtDesc(studentId, assignmentId);
        if (selectedStudentAssignmentFailed.isPresent()) {
            if (!selectedStudentAssignmentFailed.get().getIsRequestAccepted())
                throw new DuplicateEntryException("You requested earlier, so please wait for accept it");
        }

        studentAssignmentFailedRequestRepository.save(
                StudentAssignmentFailedRequest.builder()
                        .propertyId(UUID.randomUUID().toString())
                        .isRequestAccepted(false)
                        .createdAt(Instant.now())
                        .student(selectedStudentAssignment.get().getStudent())
                        .lessonAssignment(selectedStudentAssignment.get().getAssignment())
                        .build()
        );
    }

    @Override
    public void acceptRequest(String requestId) {
        if (requestId.isEmpty()) throw new EntryNotFoundException("Student request id not found");

        Optional<StudentAssignmentFailedRequest> selectedRequest = studentAssignmentFailedRequestRepository.findById(requestId);
        if (selectedRequest.isEmpty()) throw new EntryNotFoundException("Student assignment request not found");

        Optional<StudentHasAssignmentStatusTracker> selectedStudentAssignmentTracker = studentHasAssignmentStatusTrackerRepository.findByStudentAssignmentIdStudentIdAndStudentAssignmentIdAssignmentId
                (selectedRequest.get().getStudent().getPropertyId(), selectedRequest.get().getLessonAssignment().getPropertyId());
        if (selectedStudentAssignmentTracker.isEmpty())
            throw new EntryNotFoundException("Student assignment tracker not found");

        selectedStudentAssignmentTracker.get().setIsRepeatedAccepted(true);
        selectedStudentAssignmentTracker.get().setAttemps(selectedStudentAssignmentTracker.get().getAttemps() + 1);
        studentHasAssignmentStatusTrackerRepository.save(selectedStudentAssignmentTracker.get());

        selectedRequest.get().setIsRequestAccepted(Boolean.TRUE);
        studentAssignmentFailedRequestRepository.save(selectedRequest.get());
    }

    @Override
    public PaginatedFailedRequestsDTO findAllRequestsByCourseAndIntake(String course, String intake, String searchText, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<ResponseFailedRequestCustomDTO> resultPage =
                enrollmentRepository.findFailedRequestsFlexible(
                        (course == null || course.isBlank()) ? null : course,
                        (intake == null || intake.isBlank()) ? null : intake,
                        (searchText == null || searchText.isBlank()) ? null : searchText,
                        pageable
                );

        List<ResponseFailedRequestCustomDTO> list = new ArrayList<>();
        resultPage.forEach(item->{
            list.add(
                    ResponseFailedRequestCustomDTO.builder()
                            .requestId(item.getRequestId())
                            .fullName(item.getFullName())
                            .username(item.getUsername())
                            .assignmentName(item.getAssignmentName())
                            .requestState(item.getRequestState())
                            .build()
            );
        });

        return PaginatedFailedRequestsDTO.builder()
                .count(resultPage.getTotalElements())
                .dataList(list)
                .build();
    }


    @Override
    public ResponseStudentAssignmentFailedRequestDTO findByStudentAndAssignmentIds(String studentId, String assignmentId) {
        if (studentId.isEmpty() || assignmentId.isEmpty())
            throw new EntryNotFoundException("Student id or assignment id not found");

        Optional<StudentAssignmentFailedRequest> selectedData = studentAssignmentFailedRequestRepository.findFirstByStudentPropertyIdAndLessonAssignmentPropertyIdOrderByCreatedAtDesc(studentId, assignmentId);
        if (selectedData.isEmpty()) {
            return ResponseStudentAssignmentFailedRequestDTO.builder().build();
        }

        return ResponseStudentAssignmentFailedRequestDTO.builder()
                .propertyId(selectedData.get().getPropertyId())
                .isRequestAccepted(selectedData.get().getIsRequestAccepted())
                .build();
    }
}
