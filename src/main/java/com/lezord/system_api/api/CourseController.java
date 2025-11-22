package com.lezord.system_api.api;

import com.lezord.system_api.dto.request.RequestCourseDTO;
import com.lezord.system_api.dto.response.paginate.PaginatedCourseDTO;
import com.lezord.system_api.service.CourseService;
import com.lezord.system_api.util.StandardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/courses")
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER')")
    public ResponseEntity<StandardResponseDTO> getAllCourses(@RequestParam(defaultValue = "") String searchText) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Courses found")
                                .data(courseService.findAll(searchText))
                                .build()
                );
    }

    @GetMapping("/latest/visitor/view")
    public ResponseEntity<StandardResponseDTO> getAllLatestCoursesForClientExpose() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Courses found")
                                .data(courseService.findLatestCourseslist())
                                .build()
                );
    }

    @GetMapping("/by-intake/visitor/view/{intakeId}")
    public ResponseEntity<StandardResponseDTO> getAllCoursesByIntakeIdClientExpose(@PathVariable String intakeId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Courses found")
                                .data(courseService.findByIntakeId(intakeId))
                                .build()
                );
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<StandardResponseDTO> getCourseById(@PathVariable String courseId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Course found")
                                .data(courseService.findById(courseId))
                                .build()
                );
    }

    @GetMapping("/active-program-count")
    public ResponseEntity<StandardResponseDTO> getActiveProgramCount(@RequestParam String searchText) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Courses count")
                                .data(courseService.count(searchText))
                                .build()
                );
    }

    @GetMapping("/change-course-status/{courseId}")
    public ResponseEntity<StandardResponseDTO> changeCourseStatus(@PathVariable String courseId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Courses status changed")
                                .data(courseService.changeStatus(courseId))
                                .build()
                );
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> createCourse(@ModelAttribute RequestCourseDTO dto) {
        courseService.create(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        StandardResponseDTO.builder()
                                .code(201)
                                .message("Course created")
                                .data(null)
                                .build()
               );
    }

    @PutMapping("/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> updateCourse(@ModelAttribute RequestCourseDTO dto, @PathVariable String courseId) {
        courseService.update(dto,courseId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        StandardResponseDTO.builder()
                                .code(201)
                                .message("Course updated")
                                .data(null)
                                .build()
                );
    }

    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> deleteCourse(@PathVariable String courseId) {
        courseService.delete(courseId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(
                        StandardResponseDTO.builder()
                                .code(204)
                                .message("Course deleted")
                                .data(null)
                                .build()
                );
    }

    @GetMapping("/student")
    public ResponseEntity<StandardResponseDTO> getAllCoursesByStudentId(
            @RequestParam String searchText,
            @RequestParam String studentId
    ){
        PaginatedCourseDTO allCoursesByStudentId = courseService.findAllCoursesByStudentId(searchText, studentId);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        200,
                        "All Courses Related To Student",
                        allCoursesByStudentId
                ),
                HttpStatus.OK
        );
    }
}
