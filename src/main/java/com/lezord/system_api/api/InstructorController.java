package com.lezord.system_api.api;

import com.lezord.system_api.dto.request.RequestInstructorDTO;
import com.lezord.system_api.service.ApplicationUserService;
import com.lezord.system_api.service.InstructorService;
import com.lezord.system_api.util.StandardResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/instructors")
public class InstructorController {

    private final ApplicationUserService applicationUserService;
    private final InstructorService instructorService;

    @GetMapping
    public ResponseEntity<StandardResponseDTO> getAllInstructors(@RequestParam(defaultValue = "") String searchText,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Instructors found")
                                .data(instructorService.findAll(searchText, page, size))
                                .build()
                );
    }

    @GetMapping("/visitors/view")
    public ResponseEntity<StandardResponseDTO> getAllInstructorsFilteredDetails(@RequestParam(defaultValue = "") String searchText,
                                                                                @RequestParam(defaultValue = "0") int page,
                                                                                @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Instructors found")
                                .data(instructorService.findAllInstructorsForClient(searchText, page, size))
                                .build()
                );
    }

    @GetMapping("/{instructorId}")
    public ResponseEntity<StandardResponseDTO> getInstructorById(@PathVariable String instructorId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Instructor found")
                                .data(instructorService.findById(instructorId))
                                .build()
                );
    }

    @GetMapping("/by-user-id/{userId}")
    public ResponseEntity<StandardResponseDTO> getInstructorByApplicationUserId(@PathVariable String userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Instructor found")
                                .data(instructorService.findByApplicationUserId(userId))
                                .build()
                );
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> createInstructor(@Valid @RequestBody RequestInstructorDTO dto) {
        instructorService.create(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        StandardResponseDTO.builder()
                                .code(201)
                                .message("Instructor created")
                                .data(null)
                                .build()
                );
    }

    @PutMapping("/{instructorId}")
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER')")
    public ResponseEntity<StandardResponseDTO> updateInstructor(@PathVariable String instructorId,
                                                                @Valid @RequestBody RequestInstructorDTO dto) {
        instructorService.update(dto, instructorId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Instructor updated")
                                .data(null)
                                .build()
                );
    }

    @DeleteMapping("/{instructorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> deleteInstructor(@PathVariable String instructorId) {
        instructorService.delete(instructorId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(
                        StandardResponseDTO.builder()
                                .code(204)
                                .message("Instructor deleted")
                                .data(null)
                                .build()
                );
    }

    @PatchMapping("/change-status/{instructorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> changeInstructorStatus(@PathVariable String instructorId) {
        instructorService.changeStatus(instructorId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Instructor status changed")
                                .data(null)
                                .build()
                );
    }

    @PostMapping("/role/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> changeUserRoleOfInstructor(@RequestParam String roleId, @RequestParam String userId, @RequestParam boolean active) {

        applicationUserService.changeRoleForApplicationUser(roleId, userId, active);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message(active?"Create New User Role to Instructor":"Remove User Role from Instructor")
                                .data(null)
                                .build()
                );
    }

}
