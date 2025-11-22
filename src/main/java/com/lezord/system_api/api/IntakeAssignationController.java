package com.lezord.system_api.api;

import com.lezord.system_api.dto.request.RequestInstructorIntakeAssignationDTO;
import com.lezord.system_api.service.InstructorIntakeAssignationService;
import com.lezord.system_api.util.StandardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/intake-assignations")
@RequiredArgsConstructor
public class IntakeAssignationController {

    private final InstructorIntakeAssignationService instructorIntakeAssignationService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> assignIntakeToInstructor(
            @RequestBody RequestInstructorIntakeAssignationDTO dto) {

        instructorIntakeAssignationService.create(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(StandardResponseDTO.builder()
                        .code(201)
                        .message("Intake assigned to instructor successfully")
                        .data(null)
                        .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{instructorId}/{intakeId}")
    public ResponseEntity<StandardResponseDTO> updateAssignation(
            @RequestBody RequestInstructorIntakeAssignationDTO dto,
            @PathVariable String instructorId,
            @PathVariable String intakeId) {

        instructorIntakeAssignationService.update(dto, instructorId, intakeId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(StandardResponseDTO.builder()
                        .code(200)
                        .message("Assignation updated successfully")
                        .data(null)
                        .build());
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{instructorId}/{intakeId}")
    public ResponseEntity<StandardResponseDTO> deleteAssignation(
            @PathVariable String instructorId,
            @PathVariable String intakeId) {

        instructorIntakeAssignationService.delete(instructorId, intakeId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(StandardResponseDTO.builder()
                        .code(204)
                        .message("Assignation deleted successfully")
                        .data(null)
                        .build());
    }

    @GetMapping
    public ResponseEntity<StandardResponseDTO> getAllAssignations(
            @RequestParam(defaultValue = "") String searchText,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {

        return ResponseEntity
                .ok(StandardResponseDTO.builder()
                        .code(200)
                        .message("Assignations fetched successfully")
                        .data(instructorIntakeAssignationService.findAll(searchText, pageNumber, pageSize))
                        .build());
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{instructorId}/{intakeId}")
    public ResponseEntity<StandardResponseDTO> changeAssignationStatus(
            @PathVariable String instructorId,
            @PathVariable String intakeId) {

        instructorIntakeAssignationService.changeStatus(instructorId, intakeId);

        return ResponseEntity
                .ok(StandardResponseDTO.builder()
                        .code(200)
                        .message("Assignations status changed")
                        .data(null)
                        .build()
                );
    }
}
