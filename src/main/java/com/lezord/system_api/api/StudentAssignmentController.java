package com.lezord.system_api.api;

import com.lezord.system_api.dto.request.RequestStudentHasAssignmentDTO;
import com.lezord.system_api.dto.request.RequestStudentHasAssignmentUpdateDTO;
import com.lezord.system_api.dto.response.ResponseStudentHasAssignmentDTO;
import com.lezord.system_api.dto.response.paginate.PaginatedStudentHasAssignmentDTO;
import com.lezord.system_api.entity.enums.StudentHasAssignmentTypes;
import com.lezord.system_api.service.StudentHasAssignmentService;
import com.lezord.system_api.util.StandardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/student-assignment")
@RequiredArgsConstructor
public class StudentAssignmentController {
    private final StudentHasAssignmentService studentHasAssignmentService;

    @PostMapping
    public ResponseEntity<StandardResponseDTO> createStudentAssignment(
            @RequestBody RequestStudentHasAssignmentDTO dto
    ) {
        String studentAssignmentId = studentHasAssignmentService.createStudentAssignment(dto);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        201,
                        "Student Assignment Created Successfully",
                        studentAssignmentId
                ),
                HttpStatus.CREATED
        );
    }

    @PutMapping
    public ResponseEntity<StandardResponseDTO> updateStudentAssignment(
            @RequestBody RequestStudentHasAssignmentUpdateDTO dto,
            @RequestParam String studentHasAsignmentId,
            @RequestParam StudentHasAssignmentTypes type
    ) {
        studentHasAssignmentService.updateStudentAssignment(dto, studentHasAsignmentId,type);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        201,
                        "Update student has assignment",
                        null
                ),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<StandardResponseDTO> findByStudentIdAndAssignmentId(
            @RequestParam String studentId,
            @RequestParam String assignmentId
    ) {
        ResponseStudentHasAssignmentDTO selectedData = studentHasAssignmentService.findByStudentAndAssignmentIds(studentId, assignmentId);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        200,
                        "Assignment data found",
                        selectedData
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/get-all-completed-assignments")
    public ResponseEntity<StandardResponseDTO> getAllCompletedAssignmentWithStudentMarks(
            @RequestParam String studentId,
            @RequestParam(required = false) String intakeId,
            @RequestParam String contentTypeId,
            @RequestParam int page,
            @RequestParam int size
    ) {
        PaginatedStudentHasAssignmentDTO allData =
                studentHasAssignmentService
                        .getAllCompletedAssignmentWithStudentMarks(studentId, intakeId, contentTypeId, page, size);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        200,
                        "All Completed Assignments",
                        allData
                ),
                HttpStatus.OK
        );
    }
}
