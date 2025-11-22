package com.lezord.system_api.api;

import com.lezord.system_api.service.AssignmentQuestionAudioService;
import com.lezord.system_api.util.StandardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/v1/assignment-questions-audio")
@RequiredArgsConstructor
public class AssignmentQuestionAudioController {
    private final AssignmentQuestionAudioService assignmentQuestionAudioService;

    @PostMapping
    public ResponseEntity<StandardResponseDTO> uploadAudio(
            @RequestParam MultipartFile audio,
            @RequestParam String questionId
    ) {
        assignmentQuestionAudioService.uploadAudio(audio, questionId);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        201,
                        "Question Audio Upload Successfully",
                        null
                ),
                HttpStatus.CREATED
        );
    }

    @PutMapping
    public ResponseEntity<StandardResponseDTO> updateAudio(
            @RequestParam MultipartFile audio,
            @RequestParam String audioId
    ) throws SQLException {
        assignmentQuestionAudioService.updateAudio(audio, audioId);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        201,
                        "Question Audio Upload Successfully",
                        null
                ),
                HttpStatus.CREATED
        );
    }
}
