package com.lezord.system_api.api;

import com.lezord.system_api.dto.request.RequestGetInTouchDTO;
import com.lezord.system_api.entity.enums.GetInTouchStatus;
import com.lezord.system_api.service.GetInTouchService;
import com.lezord.system_api.util.StandardResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/get-in-touch")
public class GetInTouchController {

    private final GetInTouchService getInTouchService;

    @PostMapping
    public ResponseEntity<StandardResponseDTO> createMessage(@Valid @RequestBody RequestGetInTouchDTO dto) {
        getInTouchService.create(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        StandardResponseDTO.builder()
                                .code(201)
                                .message("Message created")
                                .data(null)
                                .build()
                );
    }

    @DeleteMapping("/{messageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> deleteMessage(@PathVariable String messageId) {
        getInTouchService.delete(messageId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(
                        StandardResponseDTO.builder()
                                .code(204)
                                .message("Message deleted")
                                .data(null)
                                .build()
                );
    }

    @PatchMapping("/change-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> changeMessageStatus(
            @RequestParam GetInTouchStatus status,
            @RequestParam String messageId
    ) {
        getInTouchService.changeStatus(status, messageId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Message status changed")
                                .data(null)
                                .build()
                );
    }

    @GetMapping("/count/total")
    public ResponseEntity<StandardResponseDTO> getTotalMessagesCount() {
        long count = getInTouchService.totalMessages();
        return ResponseEntity.ok(
                StandardResponseDTO.builder()
                        .code(200)
                        .message("Total message count")
                        .data(count)
                        .build()
        );
    }

    @GetMapping("/count/unseen")
    public ResponseEntity<StandardResponseDTO> getUnseenMessagesCount() {
        long count = getInTouchService.unseenMessages();
        return ResponseEntity.ok(
                StandardResponseDTO.builder()
                        .code(200)
                        .message("Unseen message count")
                        .data(count)
                        .build()
        );
    }

    @GetMapping("/unseen")
    public ResponseEntity<StandardResponseDTO> getUnseenMessages(
            @RequestParam(defaultValue = "") String searchText,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                StandardResponseDTO.builder()
                        .code(200)
                        .message("Unseen messages found")
                        .data(getInTouchService.getUnSeenMessages(searchText, page, size))
                        .build()
        );
    }

    @GetMapping("/seen")
    public ResponseEntity<StandardResponseDTO> getSeenMessages(
            @RequestParam(defaultValue = "") String searchText,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                StandardResponseDTO.builder()
                        .code(200)
                        .message("Seen messages found")
                        .data(getInTouchService.getSeenMessages(searchText, page, size))
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<StandardResponseDTO> getAllMessages(
            @RequestParam(defaultValue = "") String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                StandardResponseDTO.builder()
                        .code(200)
                        .message("All messages found")
                        .data(getInTouchService.getAll(email, page, size))
                        .build()
        );
    }
}
