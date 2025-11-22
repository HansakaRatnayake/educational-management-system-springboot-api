package com.lezord.system_api.api;

import com.lezord.system_api.dto.request.RequestIntakeInstalmentDTO;
import com.lezord.system_api.dto.response.ResponseIntakeInstallmentDTO;
import com.lezord.system_api.service.IntakeInstalmentService;
import com.lezord.system_api.util.StandardResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/intake-installments")
@RequiredArgsConstructor
public class IntakeInstallmentController {

    private final IntakeInstalmentService installmentService;

    @PostMapping("/{intakeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> createInstallment(@Valid @RequestBody List<RequestIntakeInstalmentDTO> dto, @PathVariable String intakeId) {
        installmentService.create(dto, intakeId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(StandardResponseDTO.builder()
                        .code(201)
                        .message("Installment created successfully")
                        .data(null)
                        .build());
    }

    @PutMapping("/{intakeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> updateInstallment(@Valid @RequestBody List<RequestIntakeInstalmentDTO> dto,
                                                                 @PathVariable String intakeId) {
        installmentService.update(dto, intakeId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(StandardResponseDTO.builder()
                        .code(201)
                        .message("Installment updated successfully")
                        .data(null)
                        .build());
    }

    @DeleteMapping("/{intakeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> deleteInstallment(@PathVariable String intakeId) {
        installmentService.delete(intakeId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(StandardResponseDTO.builder()
                        .code(204)
                        .message("Installment deleted successfully")
                        .data(null)
                        .build());
    }

    @PatchMapping("/change-status/{installmentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> changeStatus(@PathVariable String installmentId) {
        installmentService.changeStatus(installmentId);
        return ResponseEntity
                .ok(StandardResponseDTO.builder()
                        .code(200)
                        .message("Installment status changed successfully")
                        .data(null)
                        .build());
    }

    @GetMapping("/{installmentId}")
    public ResponseEntity<StandardResponseDTO> getInstallment(@PathVariable String installmentId) {
        ResponseIntakeInstallmentDTO installment = installmentService.findInstallmentById(installmentId);
        return ResponseEntity
                .ok(StandardResponseDTO.builder()
                        .code(200)
                        .message("Installment retrieved successfully")
                        .data(installment)
                        .build());
    }

    @GetMapping("/by-intake/{intakeId}")
    public ResponseEntity<StandardResponseDTO> getInstallmentsByIntake(@PathVariable String intakeId) {
        return ResponseEntity
                .ok(StandardResponseDTO.builder()
                        .code(200)
                        .message("Installments for intake retrieved successfully")
                        .data(installmentService.findInstallmentsByIntake(intakeId))
                        .build());
    }
}
