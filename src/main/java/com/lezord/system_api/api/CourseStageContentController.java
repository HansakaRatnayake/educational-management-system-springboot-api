package com.lezord.system_api.api;

import com.lezord.system_api.dto.request.RequestCourseStageContentDTO;
import com.lezord.system_api.dto.request.RequestUpdateCourseStageContentDTO;
import com.lezord.system_api.service.CourseStageContentService;
import com.lezord.system_api.util.StandardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/course-stage-contents")
@RequiredArgsConstructor
public class CourseStageContentController {

    private final CourseStageContentService courseStageContentService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> create(@RequestBody RequestCourseStageContentDTO dto) {
        courseStageContentService.create(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        StandardResponseDTO.builder()
                                .code(201)
                                .message("Course Stage Content created")
                                .data(null)
                                .build()
                );
    }

    @PutMapping("/{propertyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> update(@RequestBody RequestUpdateCourseStageContentDTO dto, @PathVariable String propertyId) {
        courseStageContentService.update(dto, propertyId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        StandardResponseDTO.builder()
                                .code(201)
                                .message("Course Stage Content updated")
                                .data(null)
                                .build()
                );
    }

    @DeleteMapping("/{propertyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> delete(@PathVariable String propertyId) {
        courseStageContentService.delete(propertyId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(
                        StandardResponseDTO.builder()
                                .code(204)
                                .message("Course Stage Content Deleted")
                                .data(null)
                                .build()
                );
    }

    @PatchMapping("/change-status/{propertyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> changeStatus(@PathVariable String propertyId) {
        courseStageContentService.changeStatus(propertyId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Course Stage Content status changed")
                                .data(null)
                                .build()
                );
    }

    @GetMapping("/course-stage/{courseStageId}")
    public ResponseEntity<StandardResponseDTO> getByCourseStageId(@PathVariable String courseStageId, @RequestParam int page, @RequestParam int size) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Course Stage Content found")
                                .data(courseStageContentService.getById(courseStageId, page, size))
                                .build()
                );
    }
}
