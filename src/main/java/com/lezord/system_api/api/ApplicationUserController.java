package com.lezord.system_api.api;

import com.lezord.system_api.dto.request.RequestApplicationUserDTO;
import com.lezord.system_api.dto.request.RequestApplicationUserPasswordResetDTO;
import com.lezord.system_api.service.impl.ApplicationUserServiceImpl;
import com.lezord.system_api.util.StandardResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class ApplicationUserController {

    private final ApplicationUserServiceImpl applicationUserService;

    @PostMapping("/visitor/register")
    public ResponseEntity<StandardResponseDTO> register(@Valid @RequestBody RequestApplicationUserDTO dto) {
        applicationUserService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                StandardResponseDTO.builder()
                        .code(201)
                        .message("User created")
                        .data(null)
                        .build()
        );
    }

    @PostMapping(path = {"/forgot-password-request-code"}, params = {"email"})
    public ResponseEntity<StandardResponseDTO> forgotPasswordSendVerificationCode(@RequestParam String email) {
        applicationUserService.forgotPasswordSendVerificationCode(email);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(

                        StandardResponseDTO.builder()
                        .code(201)
                        .message("Password reset verification code has been sent")
                        .data(null)
                        .build()
        );
    }

    @PostMapping(path = {"/verify-reset"}, params = {"otp", "email"})
    public ResponseEntity<StandardResponseDTO> resetPassword(@RequestParam String otp, @RequestParam String email) {
        boolean isVerified = applicationUserService.verifyReset(otp, email);
        return ResponseEntity
                .status(isVerified ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(

                        StandardResponseDTO.builder()
                        .code(isVerified ? 200 : 400)
                        .message(isVerified ? "Reset your password" : "Invalid OTP. Please enter the correct code to verify your email address.")
                        .data(isVerified)
                        .build()
        );
    }

    @PostMapping(path = {"/reset-password"})
    public ResponseEntity<StandardResponseDTO> passwordReset(@Valid @RequestBody RequestApplicationUserPasswordResetDTO dto) {
        return ResponseEntity.ok(
                StandardResponseDTO.builder()
                        .code(200)
                        .message("Password reset completed successfully")
                        .data(applicationUserService.passwordReset(dto))
                        .build()
        );
    }

    @GetMapping("/{username}")
    public ResponseEntity<StandardResponseDTO> getApplicationUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(
                StandardResponseDTO.builder()
                        .code(200)
                        .message("Application User Found")
                        .data(applicationUserService.getApplicationUserByUsername(username))
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> getAllApplicationUsers(@RequestParam String searchText, int page, int size) {
        return ResponseEntity.ok(
                StandardResponseDTO.builder()
                        .code(200)
                        .message("Application Users found")
                        .data(applicationUserService.findAll(searchText, page, size))
                        .build()
        );
    }

    @GetMapping("/role/{username}")
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER','STUDENT')")
    public ResponseEntity<StandardResponseDTO> getApplicationUserRoleByUsername(@PathVariable String username) {
        return ResponseEntity.ok(
                StandardResponseDTO.builder()
                        .code(200)
                        .message("Application user role found")
                        .data(applicationUserService.findUserRoleByUsername(username))
                        .build()
        );
    }


//    @PutMapping
//    public ResponseEntity<StandardResponseDTO> updateApplicationUser(@Valid @RequestBody String username) {
//
//        applicationUserService.update(username);
//        return ResponseEntity.ok(
//                StandardResponseDTO.builder()
//                        .code(200)
//                        .message("Application user updated")
//                        .data(null)
//                        .build()
//        );
//    }

//    @DeleteMapping("/{userId}")
//    public ResponseEntity<StandardResponseDTO> deleteApplicationUser(String userId) {
//        applicationUserService.delete(userId);
//        return ResponseEntity
//                .status(HttpStatus.NO_CONTENT)
//                .body(
//                        StandardResponseDTO.builder()
//                                .code(204)
//                                .message("Application user updated")
//                                .data(null)
//                                .build()
//
//        );
//    }
}
