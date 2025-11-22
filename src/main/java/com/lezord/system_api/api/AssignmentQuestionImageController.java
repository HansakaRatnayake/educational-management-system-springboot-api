package com.lezord.system_api.api;

import com.lezord.system_api.service.AssignmentQuestionImageService;
import com.lezord.system_api.util.StandardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/v1/assignment-questions-image")
@RequiredArgsConstructor
public class AssignmentQuestionImageController {
    private final AssignmentQuestionImageService assignmentQuestionImageService;

    @PostMapping
    public ResponseEntity<StandardResponseDTO> uploadImage(
            @RequestParam MultipartFile image,
            @RequestParam String questionId
    ) {
        assignmentQuestionImageService.uploadImage(image, questionId);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        201,
                        "Question Image Uploaded Successfully",
                        null
                ),
                HttpStatus.CREATED
        );
    }

    @PutMapping
    public ResponseEntity<StandardResponseDTO> updateImage(
            @RequestParam MultipartFile image,
            @RequestParam String imageId
    ) throws SQLException {
        assignmentQuestionImageService.updateImage(image, imageId);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        201,
                        "Question Image Upload Successfully",
                        null
                ),
                HttpStatus.CREATED
        );
    }
}
