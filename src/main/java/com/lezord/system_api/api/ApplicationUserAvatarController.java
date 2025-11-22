package com.lezord.system_api.api;

import com.lezord.system_api.service.ApplicationUserAvatarService;
import com.lezord.system_api.util.StandardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-avatars")
public class ApplicationUserAvatarController {

    private final ApplicationUserAvatarService applicationUserAvatarService;

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER','STUDENT')")
    public ResponseEntity<StandardResponseDTO> getUserAvatarByUserId(@PathVariable String userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("User avatar found")
                                .data(applicationUserAvatarService.findByUserId(userId))
                                .build()
                );
    }

    @PostMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER','STUDENT')")
    public ResponseEntity<StandardResponseDTO> createUserAvatar(@RequestParam("userAvatar") MultipartFile file, @PathVariable String userId) {
        applicationUserAvatarService.create(file, userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("User avatar created")
                                .data(null)
                                .build()
                );
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER','STUDENT')")
    public ResponseEntity<StandardResponseDTO> updateUserAvatar(@RequestParam("userAvatar") MultipartFile file, @PathVariable String userId) {
        applicationUserAvatarService.update(file, userId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        StandardResponseDTO.builder()
                                .code(201)
                                .message("User avatar updated")
                                .data(null)
                                .build()
                );
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER','STUDENT')")
    public ResponseEntity<StandardResponseDTO> deleteUserAvatar(@PathVariable String userId) {
        applicationUserAvatarService.delete(userId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(
                        StandardResponseDTO.builder()
                                .code(204)
                                .message("User avatar deleted")
                                .data(null)
                                .build()
                );
    }
}
