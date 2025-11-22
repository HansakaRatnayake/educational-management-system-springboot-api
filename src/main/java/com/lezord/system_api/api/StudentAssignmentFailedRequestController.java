package com.lezord.system_api.api;

import com.lezord.system_api.dto.response.ResponseStudentAssignmentFailedRequestDTO;
import com.lezord.system_api.dto.response.paginate.PaginatedFailedRequestsDTO;
import com.lezord.system_api.service.StudentAssignmentFailedRequestService;
import com.lezord.system_api.util.StandardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/assignment-failed-request")
@RequiredArgsConstructor
public class StudentAssignmentFailedRequestController {
    private final StudentAssignmentFailedRequestService studentAssignmentFailedRequestService;

    @PostMapping
    public ResponseEntity<StandardResponseDTO> createAssignmentRequest(
            @RequestParam String studentId,
            @RequestParam String assignmentId
    ){
        studentAssignmentFailedRequestService.createRequest(studentId,assignmentId);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        201,
                        "Assignment Request Created",
                        null
                ),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/{requestId}")
    public ResponseEntity<StandardResponseDTO> acceptRequest(
            @PathVariable String requestId
    ){
        studentAssignmentFailedRequestService.acceptRequest(requestId);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        201,
                        "Request status changed",
                        null
                ),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<StandardResponseDTO> findByStudentAndAssignmentIds(
            @RequestParam String studentId,
            @RequestParam String assignmentId
    ){
        ResponseStudentAssignmentFailedRequestDTO data = studentAssignmentFailedRequestService.findByStudentAndAssignmentIds(studentId, assignmentId);

        return new ResponseEntity<>(
                new StandardResponseDTO(
                        200,
                        "Student request data found",
                        data
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/get-all-requests-by-course-and-intake")
    public ResponseEntity<StandardResponseDTO> getFailedRequestsByCourseIdAndIntakeId(
            @RequestParam(required = false) String courseId,
            @RequestParam(required = false) String intakeId,
            @RequestParam String searchText,
            @RequestParam int page,
            @RequestParam int size
    ){
        PaginatedFailedRequestsDTO allData = studentAssignmentFailedRequestService.findAllRequestsByCourseAndIntake(courseId, intakeId, searchText, page, size);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        200,
                        "Requests Found",
                        allData
                ),
                HttpStatus.OK
        );
    }
}
