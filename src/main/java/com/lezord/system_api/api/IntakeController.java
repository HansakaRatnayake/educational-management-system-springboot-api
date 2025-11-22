package com.lezord.system_api.api;

import com.lezord.system_api.dto.request.RequestIntakeDTO;
import com.lezord.system_api.dto.response.ResponseIntakeDTO;
import com.lezord.system_api.service.IntakeService;
import com.lezord.system_api.util.StandardResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/intakes")
@RequiredArgsConstructor
public class IntakeController {

    private final IntakeService intakeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> createIntake(@Valid @RequestBody RequestIntakeDTO dto) {
        intakeService.create(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(StandardResponseDTO.builder()
                        .code(201)
                        .message("Intake created successfully")
                        .data(null)
                        .build());
    }

    @PutMapping("/{intakeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> updateIntake(@Valid @RequestBody RequestIntakeDTO dto, @PathVariable String intakeId) {
        intakeService.update(dto, intakeId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        StandardResponseDTO.builder()
                        .code(200)
                        .message("Intake updated successfully")
                        .data(null)
                        .build());
    }

    @DeleteMapping("/{intakeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> deleteIntake(@PathVariable String intakeId) {
        intakeService.delete(intakeId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(
                        StandardResponseDTO.builder()
                        .code(204)
                        .message("Intake deleted successfully")
                        .data(null)
                        .build());
    }

    @PatchMapping("/change-status/{intakeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> changeIntakeStatus(@PathVariable String intakeId) {
        intakeService.changeStatus(intakeId);
        return ResponseEntity
                .ok(StandardResponseDTO.builder()
                        .code(200)
                        .message("Intake status changed successfully")
                        .data(null)
                        .build());
    }

    @GetMapping("/{intakeId}")
    public ResponseEntity<StandardResponseDTO> getIntake(@PathVariable String intakeId) {
        ResponseIntakeDTO intake = intakeService.findById(intakeId);
        return ResponseEntity
                .ok(StandardResponseDTO.builder()
                        .code(200)
                        .message("Intake retrieved successfully")
                        .data(intake)
                        .build());
    }

    @GetMapping
    public ResponseEntity<StandardResponseDTO> getAllIntakes(
            @RequestParam(required = false) String courseId,
            @RequestParam(required = false) String instructorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity
                .ok(StandardResponseDTO.builder()
                        .code(200)
                        .message("Intakes retrieved successfully")
                        .data(intakeService.findAll(courseId, instructorId, page, size))
                        .build());
    }

    @GetMapping("/student")
    public ResponseEntity<StandardResponseDTO> getAllIntakesForStudent(
            @RequestParam(required = false) String courseId,
            @RequestParam() String studentId
    ) {
        return ResponseEntity
                .ok(StandardResponseDTO.builder()
                        .code(200)
                        .message("Intakes for student retrieved successfully")
                        .data(intakeService.findAllForStudent(courseId, studentId))
                        .build());
    }
}
