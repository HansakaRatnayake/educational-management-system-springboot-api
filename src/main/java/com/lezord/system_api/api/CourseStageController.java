package com.lezord.system_api.api;

import com.lezord.system_api.dto.request.RequestCourseStageDTO;
import com.lezord.system_api.dto.response.ResponseAuthenticatedCourseStageTypeDTO;
import com.lezord.system_api.dto.response.ResponseClientCourseStageTypeDTO;
import com.lezord.system_api.service.CourseStageService;
import com.lezord.system_api.util.StandardResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/course-stages")
public class CourseStageController {

    private final CourseStageService courseStageService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> createCourseStage(@Valid @RequestBody RequestCourseStageDTO dto) {
        courseStageService.create(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(StandardResponseDTO.builder()
                        .code(201)
                        .message("CourseStage created successfully")
                        .data(null)
                        .build());
    }

    @PutMapping("/{courseStageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> updateCourseStage(@Valid @RequestBody RequestCourseStageDTO dto, @PathVariable String courseStageId) {
        courseStageService.update(dto, courseStageId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(StandardResponseDTO.builder()
                        .code(201)
                        .message("CourseStage updated successfully")
                        .data(null)
                        .build());
    }

    @DeleteMapping("/{courseStageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> deleteCourseStage(@PathVariable String courseStageId) {
        courseStageService.delete(courseStageId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(StandardResponseDTO.builder()
                        .code(204)
                        .message("CourseStage deleted successfully")
                        .data(null)
                        .build());
    }

    @GetMapping
    public ResponseEntity<StandardResponseDTO> getCourseStagesByCourseAndType(@RequestParam String courseId,
                                                                              @RequestParam String courseContentTypeId,
                                                                              @RequestParam int page,
                                                                              @RequestParam int size) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(StandardResponseDTO.builder()
                        .code(200)
                        .message("CourseStages retrieved successfully")
                        .data(courseStageService.findAllByCourseAndStageContentType(courseId, courseContentTypeId, page, size))
                        .build());
    }

    @GetMapping("/all-stages/{courseId}")
    public ResponseEntity<StandardResponseDTO> getAllStagesAndDataByCourseId(
            @PathVariable String courseId
    ){
        List<ResponseClientCourseStageTypeDTO> allData = courseStageService.getAllStagesWithData(courseId);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        200,
                        "All Stages With Data",
                        allData
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/all-stages-for-authenticated/{courseId}")
    public ResponseEntity<StandardResponseDTO> getAllStagesAndDataByCourseIdAuthenticated(
            @PathVariable String courseId,
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) String intakeId
    ){
        List<ResponseAuthenticatedCourseStageTypeDTO> allData
                = courseStageService.getAllStagesWithDataForAuthenticated(courseId,studentId,intakeId);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        200,
                        "All Stages With Data",
                        allData
                ),
                HttpStatus.OK
        );
    }

}
