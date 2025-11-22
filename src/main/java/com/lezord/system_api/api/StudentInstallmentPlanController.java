package com.lezord.system_api.api;

import com.lezord.system_api.service.StudentInstallmentPlanService;
import com.lezord.system_api.util.StandardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/student-installment-plans")
public class StudentInstallmentPlanController {
    private final StudentInstallmentPlanService studentInstallmentPlanService;

    @GetMapping("/student/{studentId}/{intakeId}")
    public ResponseEntity<StandardResponseDTO> searchStudents(
            @PathVariable String studentId,
            @PathVariable String intakeId
    ) {
        return ResponseEntity.ok(
                StandardResponseDTO.builder()
                        .code(200)
                        .message("student installment plan found")
                        .data(studentInstallmentPlanService.getStudentInstallmentPlans(studentId,intakeId))
                        .build()
        );
    }
}
