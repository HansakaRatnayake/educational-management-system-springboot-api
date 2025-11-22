package com.lezord.system_api.api;

import com.lezord.system_api.dto.request.RequestLessonAssignmentDTO;
import com.lezord.system_api.dto.response.ResponseLessonAssignmentDTO;
import com.lezord.system_api.dto.response.paginate.PaginatedLessonAssignmentDTO;
import com.lezord.system_api.entity.enums.LessonAssignmentStatusTypes;
import com.lezord.system_api.service.LessonAssignmentService;
import com.lezord.system_api.util.StandardResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
public class LessonAssignmentController {
    private final LessonAssignmentService lessonAssignmentService;

    @PostMapping()
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER')")
    public ResponseEntity<StandardResponseDTO> createAssignment(
            @Valid @RequestBody RequestLessonAssignmentDTO dto,
            @RequestParam String lessonId,
            @RequestParam String intakeId
    ) {
        String assignmentId = lessonAssignmentService.createAssignment(dto, lessonId,intakeId);
        return new ResponseEntity<>(
                new StandardResponseDTO(201,"Assignment Create Successfully",assignmentId),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{assignmentId}")
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER')")
    public ResponseEntity<StandardResponseDTO> updateAssignment(
            @RequestBody RequestLessonAssignmentDTO dto,
            @PathVariable String assignmentId
    ){
        lessonAssignmentService.updateAssignment(dto,assignmentId);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        201,
                        "Assignment Updated Successfully",
                        null
                ),
                HttpStatus.CREATED
        );
    }

    @PatchMapping
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER')")
    public ResponseEntity<StandardResponseDTO> changeStatus(
            @Valid @RequestParam LessonAssignmentStatusTypes statusType,
            @RequestParam String assignmentId
    ){
        lessonAssignmentService.changeStatus(statusType,assignmentId);
        return new ResponseEntity<>(
                new StandardResponseDTO(201,"Assignment Status Changed",null),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping("/{assignmentId}")
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER')")
    public ResponseEntity<StandardResponseDTO> deleteAssignment(
            @PathVariable String assignmentId
    ){
        lessonAssignmentService.deleteAssignment(assignmentId);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        200,
                        "Assignment Deleted Successfully",
                        null
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/all")
    public ResponseEntity<StandardResponseDTO> getAllAssignments(
            @RequestParam String lessonId,
            @RequestParam String intakeId,
            @RequestParam(required = false) String studentId,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(defaultValue = "false") Boolean areOnlyActivated
    ){
        PaginatedLessonAssignmentDTO allData = lessonAssignmentService.getAllByLessonIdAndIntake(lessonId,intakeId, studentId, page, size,areOnlyActivated);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        200,
                        "All Assignments Data",
                        allData
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/get-by-student-id-and-course-id")
    public ResponseEntity<StandardResponseDTO> getIntakeIdByCourseAndStudentIds(
            @RequestParam String studentId,
            @RequestParam String courseId
    ){
        String id = lessonAssignmentService.getIntakeIdByCourseAndStudentIds(courseId, studentId);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        200,
                        "Intake id found",
                        id
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/{assignmentId}")
    public ResponseEntity<StandardResponseDTO> findByAssignmentId(
            @PathVariable String assignmentId
    ){
        ResponseLessonAssignmentDTO data = lessonAssignmentService.findById(assignmentId);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        200,
                        data.getTitle() + " Assignment data found",
                        data
                ),
                HttpStatus.OK
        );
    }
}
