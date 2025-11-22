package com.lezord.system_api.api;

import com.lezord.system_api.dto.request.RequestStudentDTO;
import com.lezord.system_api.service.StudentService;
import com.lezord.system_api.util.StandardResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/students")
public class StudentController {

    private final StudentService studentService;


//    @GetMapping
//    public ResponseEntity<StandardResponseDTO> getAllStudents(@RequestParam String searchText, @RequestParam int page, @RequestParam int size) {
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(
//                        StandardResponseDTO.builder()
//                                .code(200)
//                                .message("Students found")
//                                .data(studentService.findAll(searchText, page, size))
//                                .build()
//                );
//    }

    @GetMapping("/search")
    public ResponseEntity<StandardResponseDTO> searchStudents(
            @RequestParam(defaultValue = "") String searchText,
            @RequestParam(defaultValue = "") String courseId,
            @RequestParam(defaultValue = "") String intakeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {


        return ResponseEntity.ok(
                StandardResponseDTO.builder()
                        .code(200)
                        .message("Filtered students")
                        .data(studentService.findAll(searchText, courseId, intakeId, page, size))
                        .build()
        );
    }

    @GetMapping("/by-user-id/{userId}")
    public ResponseEntity<StandardResponseDTO> getStudentByUserId(@PathVariable String userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Student data found")
                                .data(studentService.findByUserId(userId))
                                .build()
                );
    }

    @GetMapping("/by-id/{studentId}")
    public ResponseEntity<StandardResponseDTO> getStudentById(@PathVariable String studentId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Student found")
                                .data(studentService.findById(studentId))
                                .build()
                );
    }

    @GetMapping("/by-intake/{intakeId}")
    public ResponseEntity<StandardResponseDTO> getStudentByIntake(@PathVariable String intakeId,@RequestParam int page, @RequestParam int size) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Students found")
                                .data(studentService.findByIntakeId(intakeId,page,size))
                                .build()
                );
    }

    @GetMapping("/active-student-count")
    public ResponseEntity<StandardResponseDTO> getActiveStudentCount() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Students found")
                                .data(studentService.countStudentByActiveState(true))
                                .build()
                );
    }

    @PostMapping
    public ResponseEntity<StandardResponseDTO> createStudent(@Valid @RequestBody RequestStudentDTO dto) {
        studentService.create(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        StandardResponseDTO.builder()
                                .code(201)
                                .message("Students created")
                                .data(null)
                                .build()
                );
    }

    @PutMapping("/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER','STUDENT')")
    public ResponseEntity<StandardResponseDTO> updateStudent(@Valid @RequestBody RequestStudentDTO dto, @PathVariable String studentId) {
        studentService.update(dto,studentId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        StandardResponseDTO.builder()
                                .code(201)
                                .message("Students updated")
                                .data(null)
                                .build()
                );
    }

    @DeleteMapping("/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> deleteStudent(@PathVariable String studentId) {
        studentService.delete(studentId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(
                        StandardResponseDTO.builder()
                                .code(204)
                                .message("Students deleted")
                                .data(null)
                                .build()
                );
    }

    @GetMapping("/non-paid-students/list")
    public ResponseEntity<StandardResponseDTO> getNonPaidStudentsList(
            @RequestParam(defaultValue = "") String searchText,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Non Paid Students found")
                                .data(studentService.findNonPaidStudents(searchText, page, size))
                                .build()
                );
    }

}
