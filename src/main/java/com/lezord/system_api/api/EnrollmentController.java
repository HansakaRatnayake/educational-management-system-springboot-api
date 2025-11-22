package com.lezord.system_api.api;

import com.lezord.system_api.dto.response.paginate.PaginatedEnrollmentDTO;
import com.lezord.system_api.service.EnrollmentService;
import com.lezord.system_api.util.StandardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/enrollment")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<StandardResponseDTO> createEnrollment(
            @RequestParam String studentId,
            @RequestParam String intakeId
    ){
        enrollmentService.createEnrollment(studentId,intakeId);
        return new ResponseEntity<>(
                new StandardResponseDTO(201,"Enrollment Create Successfully",null),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/all")
    public ResponseEntity<StandardResponseDTO> getAllEnrollments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1") int size
    ){
        PaginatedEnrollmentDTO allEnrollments = enrollmentService.getAllEnrollments(page, size);
        return new ResponseEntity<>(
                new StandardResponseDTO(200,"All Enrollments Data",allEnrollments),
                HttpStatus.OK
        );
    }

    @GetMapping("/verify-eligibility/{intakeId}/{studentId}")
    public ResponseEntity<StandardResponseDTO> verifyEligibility(
            @PathVariable String intakeId,
            @PathVariable String studentId
    ) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Eligibility checked")
                                .data(enrollmentService.checkEnrollmentEligibility(intakeId,studentId))
                                .build()
                );
    }

    @PatchMapping("/{enrollmentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> changeActiveStatus(
            @PathVariable String enrollmentId
    ){
        enrollmentService.changeActiveStatus(enrollmentId);
        return new ResponseEntity<>(
                new StandardResponseDTO(201,"Status Updated",null),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/change-course-access")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> changeCourseAccessStatus(
            @RequestParam(defaultValue = "") String studentId,
            @RequestParam(defaultValue = "") String intakeId
    ){
        enrollmentService.changeCourseAccessStatus(studentId, intakeId);
        return new ResponseEntity<>(
                new StandardResponseDTO(201,"Status Updated",null),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/change-all-course-access")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> changeAllCourseAccessStatus(@RequestParam boolean access){
       enrollmentService.changeAllNonPaidStudentsCourseAccessStatus(access);
        return new ResponseEntity<>(
                new StandardResponseDTO(201,"Status Updated",null),
                HttpStatus.CREATED
        );
    }

}
