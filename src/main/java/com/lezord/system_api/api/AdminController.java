package com.lezord.system_api.api;

import com.lezord.system_api.dto.request.RequestAdminDTO;
import com.lezord.system_api.dto.request.RequestApplicationUserByAdminDTO;
import com.lezord.system_api.dto.request.RequestUpdateApplicationUserDTO;
import com.lezord.system_api.service.AdminService;
import com.lezord.system_api.service.ApplicationUserService;
import com.lezord.system_api.util.StandardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final ApplicationUserService applicationUserService;
    private final AdminService adminService;

    @PostMapping("/create/user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> createUserByAdmin(@RequestBody RequestApplicationUserByAdminDTO dto) {

        applicationUserService.createUserByAdmin(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        StandardResponseDTO.builder()
                        .code(201)
                        .message("User created")
                        .data(null)
                        .build()
        );
    }

    @PutMapping("/update/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> updateUserByAdmin(@RequestBody RequestUpdateApplicationUserDTO dto, @PathVariable String userId) {

        applicationUserService.update(dto, userId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        StandardResponseDTO.builder()
                                .code(201)
                                .message("User updated")
                                .data(null)
                                .build()
                );
    }

    @DeleteMapping("/delete/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> deleteUserByAdmin(@PathVariable String userId) {

        applicationUserService.delete(userId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(
                        StandardResponseDTO.builder()
                                .code(204)
                                .message("User deleted")
                                .data(null)
                                .build()
                );
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> getAllSystemAdmins() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(201)
                                .message("Admin users found")
                                .data(applicationUserService.findAllByRole("ADMIN"))
                                .build()
                );
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> getSystemAdminByAdmin(@PathVariable String userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(201)
                                .message("Admin user found")
                                .data(adminService.getByApplicationUserId(userId))
                                .build()
                );
    }

    @PutMapping("/{adminId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> updateSystemAdmin(@RequestBody RequestAdminDTO dto, @PathVariable String adminId){
        adminService.update(dto,adminId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        StandardResponseDTO.builder()
                                .code(201)
                                .message("Admin user updated")
                                .data(null)
                                .build()
                );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/change-admin-user-status/{adminId}")
    public ResponseEntity<StandardResponseDTO> changeSystemAdminStatus(@PathVariable String adminId){
        adminService.changeStatus(adminId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        StandardResponseDTO.builder()
                                .code(201)
                                .message("Admin user updated")
                                .data(null)
                                .build()
                );
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/role/assign")
    public ResponseEntity<StandardResponseDTO> changeUserRoleOfApplicationUser(@RequestParam String roleId, @RequestParam String userId, @RequestParam boolean active) {
        applicationUserService.changeRoleForApplicationUser(roleId, userId, active);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message(active?"Create New User Role to Application User":"Remove User Role from Application User")
                                .data(null)
                                .build()
                );
    }


}
