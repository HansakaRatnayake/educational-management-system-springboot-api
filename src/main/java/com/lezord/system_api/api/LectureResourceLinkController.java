package com.lezord.system_api.api;

import com.lezord.system_api.dto.request.RequestLectureResourceLinkDTO;
import com.lezord.system_api.service.LectureResourceLinkService;
import com.lezord.system_api.util.StandardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/v1/lecture-resource-links")
@RequiredArgsConstructor
public class LectureResourceLinkController {
    private final LectureResourceLinkService lectureResourceLinkService;

    @PostMapping("/trainer/create/{recordId}")
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER')")
    public ResponseEntity<StandardResponseDTO> create(@RequestBody RequestLectureResourceLinkDTO dto, @PathVariable String recordId) throws SQLException {
        lectureResourceLinkService.create(dto,recordId);
        return new ResponseEntity<>(
                new StandardResponseDTO(201, "Lecture Resource Link Saved!", null),
                HttpStatus.CREATED
        );
    }
    @DeleteMapping("/trainer/delete/{resourceId}")
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER')")
    public ResponseEntity<StandardResponseDTO> delete(@PathVariable String resourceId) {
        lectureResourceLinkService.delete(resourceId);
        return new ResponseEntity<>(
                new StandardResponseDTO(204, "Lecture Resource Link was Deleted!", null),
                HttpStatus.NO_CONTENT
        );
    }
}
