package com.lezord.system_api.api;

import com.lezord.system_api.service.DashboardService;
import com.lezord.system_api.util.StandardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dashboard-details")
public class DashboardController {

    private final DashboardService dashboardService;


    @GetMapping("/student/enrolled/list/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN','TRAINER')")
    public ResponseEntity<StandardResponseDTO> getStudentDashboardDetails(@PathVariable String studentId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Student enrolled course details found")
                                .data(dashboardService.getStudentEnrolledCourses(studentId))
                                .build()
                );
    }

    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StandardResponseDTO> getStudentDashboardDetails(@RequestParam String studentId, @RequestParam(defaultValue = "") String intakeId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Student dashboard details found")
                                .data(dashboardService.getStudentDashboardDetail(studentId, intakeId))
                                .build()
                );
    }

    @GetMapping("/revenue/monthly/{year}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> getMonthlyRevenue(@PathVariable String year) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Monthly revenue calculated")
                                .data(dashboardService.calculateMonthlyRevenue(year))
                                .build()
                );
    }

    @GetMapping("/revenue/yearly/{year}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> getYearlyRevenue(@PathVariable String year){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Yearly revenue calculated")
                                .data(dashboardService.calculateYearlyRevenue(year))
                                .build()
                );
    }

    @GetMapping("/revenue/total")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> getTotalRevenue(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Total revenue calculated")
                                .data(dashboardService.totalRevenue())
                                .build()
                );
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> getAdminDashboardDetails() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Admin dashboard details found")
                                .data(dashboardService.getAdminDashboardStatCardDetails())
                                .build()
                );
    }

    @GetMapping("/instructor/assignation/list/{instructorId}")
    @PreAuthorize("hasAnyRole('TRAINER','ADMIN')")
    public ResponseEntity<StandardResponseDTO> getInstructorAssignedIntakeDetails(@PathVariable String instructorId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Instructor assigned intake details found")
                                .data(dashboardService.getInstructorAssignedCourses(instructorId))
                                .build()
                );
    }

    @GetMapping("/instructorId")
    @PreAuthorize("hasAnyRole('TRAINER','ADMIN')")
    public ResponseEntity<StandardResponseDTO> getInstructorDashboardDetail(@RequestParam String instructorId, @RequestParam(defaultValue = "") String intakeId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Instructor dashboard details found")
                                .data(dashboardService.getInstructorDashboardDetail(instructorId, intakeId))
                                .build()
                );
    }
}
