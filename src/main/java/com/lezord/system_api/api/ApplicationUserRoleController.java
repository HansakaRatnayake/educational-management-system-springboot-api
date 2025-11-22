package com.lezord.system_api.api;

import com.lezord.system_api.dto.request.RequestApplicationRoleDTO;
import com.lezord.system_api.service.ApplicationUserRoleService;
import com.lezord.system_api.util.StandardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class ApplicationUserRoleController {

    private final ApplicationUserRoleService applicationUserRoleService;

    @GetMapping("/visitor")
    public ResponseEntity<StandardResponseDTO> getAllApplicationUserRoles() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Roles found")
                                .data(applicationUserRoleService.findAll())
                                .build()
                );
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> createRole(
            @Validated @RequestBody RequestApplicationRoleDTO requestDTO
    ) {
        applicationUserRoleService.create(requestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        StandardResponseDTO.builder()
                                .code(201)
                                .message("Role created successfully")
                                .data(null)
                                .build()
        );
    }

    @PutMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> updateRole(
            @PathVariable String roleId,
            @Validated @RequestBody RequestApplicationRoleDTO requestDTO
    ) {
        applicationUserRoleService.update(requestDTO, roleId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        StandardResponseDTO.builder()
                                .code(201)
                                .message("Role updated successfully")
                                .data(null)
                                .build()
        );
    }

    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> deleteRole(@PathVariable String roleId) {
        applicationUserRoleService.delete(roleId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(
                        StandardResponseDTO.builder()
                                .code(204)
                                .message("Role deleted successfully")
                                .data(null)
                                .build()
        );
    }
}