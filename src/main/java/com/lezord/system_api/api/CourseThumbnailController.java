package com.lezord.system_api.api;

import com.lezord.system_api.service.CourseThumbnailService;
import com.lezord.system_api.util.StandardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/course-thumbnail")
public class CourseThumbnailController {

    private final CourseThumbnailService courseThumbnailService;

    @PutMapping("/{courseId}")
    public ResponseEntity<StandardResponseDTO> updateCourseThumbnail(@RequestParam("courseThumbnail") MultipartFile courseThumbnail, @PathVariable String courseId) {
        courseThumbnailService.update(courseThumbnail, courseId);
        return new ResponseEntity<>(
                new StandardResponseDTO(201,"Thumbnail updated",null),
                HttpStatus.CREATED
        );
    }

}
