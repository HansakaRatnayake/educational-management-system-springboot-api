package com.lezord.system_api.api;

import com.lezord.system_api.dto.request.RequestPendingInstructorRegistrationDetailDTO;
import com.lezord.system_api.service.impl.PendingInstructorRegistrationDetailServiceImpl;
import com.lezord.system_api.util.StandardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pending-instructors")
public class PendingInstructorRegistrationDetailController {

    private final PendingInstructorRegistrationDetailServiceImpl pendingInstructorRegistrationDetailService;

    @GetMapping
    public ResponseEntity<StandardResponseDTO> getPendingInstructorRegistrationDetails(@RequestParam String searchText, @RequestParam int page, @RequestParam int size) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Pending Instructor Registration Details Found")
                                .data(pendingInstructorRegistrationDetailService.findAll(searchText,page,size))
                                .build()
                );
    }

    @GetMapping("/{pendingInstructorRegistrationId}")
    public ResponseEntity<StandardResponseDTO> getPendingInstructorRegistrationDetailById(@PathVariable String pendingInstructorRegistrationId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Pending Instructor Registration Detail Found")
                                .data(pendingInstructorRegistrationDetailService.findById(pendingInstructorRegistrationId))
                                .build()
                );
    }

    @PostMapping("/register/request")
    public ResponseEntity<StandardResponseDTO> createPendingInstructorRegistrationDetail(@RequestBody RequestPendingInstructorRegistrationDetailDTO dto) {
        pendingInstructorRegistrationDetailService.create(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        StandardResponseDTO.builder()
                                .code(201)
                                .message("Instructor Registration Request Sent")
                                .data(null)
                                .build()
                );
    }

    @DeleteMapping("/{pendingInstructorRegistrationId}")
    public ResponseEntity<StandardResponseDTO> createPendingInstructorRegistrationDetail(@PathVariable String pendingInstructorRegistrationId) {
        pendingInstructorRegistrationDetailService.delete(pendingInstructorRegistrationId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(
                        StandardResponseDTO.builder()
                                .code(204)
                                .message("Pending Instructor Registration Detail Deleted")
                                .data(null)
                                .build()
                );
    }
}
