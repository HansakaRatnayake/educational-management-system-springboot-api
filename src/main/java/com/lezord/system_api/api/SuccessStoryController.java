package com.lezord.system_api.api;

import com.lezord.system_api.dto.request.RequestSuccessStoryDTO;
import com.lezord.system_api.dto.response.ResponseSuccessStoryDTO;
import com.lezord.system_api.entity.enums.SuccessStoryStatus;
import com.lezord.system_api.service.SuccessStoryService;
import com.lezord.system_api.util.StandardResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/success-stories")
public class SuccessStoryController {

    private final SuccessStoryService successStoryService;

    @PostMapping
    public ResponseEntity<StandardResponseDTO> createSuccessStory(@Valid @RequestBody RequestSuccessStoryDTO dto) {
        successStoryService.create(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        StandardResponseDTO.builder()
                                .code(201)
                                .message("Success story created")
                                .data(null)
                                .build()
                );
    }

    @PutMapping("/{userId}")
    public ResponseEntity<StandardResponseDTO> updateSuccessStory(@Valid @RequestBody RequestSuccessStoryDTO dto, @PathVariable String userId) {
        successStoryService.update(dto, userId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        StandardResponseDTO.builder()
                                .code(201)
                                .message("Success story updated")
                                .data(null)
                                .build()
                );
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> deleteSuccessStory(@PathVariable String userId) {
        successStoryService.delete(userId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(
                        StandardResponseDTO.builder()
                                .code(204)
                                .message("Success story deleted")
                                .data(null)
                                .build()
                );
    }

    @PatchMapping("/change-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> changeSuccessStoryStatus(
            @RequestParam SuccessStoryStatus status,
            @RequestParam String storyId
    ) {
        successStoryService.changeStatus(status,storyId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Success story status changed")
                                .data(null)
                                .build()
                );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<StandardResponseDTO> getSuccessStoryByUserId(@PathVariable String userId) {
        ResponseSuccessStoryDTO result = successStoryService.getByUserId(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Success story found")
                                .data(result)
                                .build()
                );
    }

    @GetMapping
    public ResponseEntity<StandardResponseDTO> getAllSuccessStories(
            @RequestParam(defaultValue = "") String searchText,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Success stories found")
                                .data(successStoryService.getAll(searchText, page, size))
                                .build()
                );
    }

    @GetMapping("/pending/list")
    public ResponseEntity<StandardResponseDTO> getPendingSuccessStories(
            @RequestParam(defaultValue = "") String searchText,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Success story found")
                                .data(successStoryService.getPendingStories(searchText, page, size))
                                .build()
                );
    }
}
