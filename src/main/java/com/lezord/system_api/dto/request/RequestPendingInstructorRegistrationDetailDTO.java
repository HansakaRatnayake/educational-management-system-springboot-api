package com.lezord.system_api.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.*;



@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestPendingInstructorRegistrationDetailDTO {

    @NotBlank(message = "username required")
    private String username;

    @NotBlank(message = "password required")
    private String password;

    @NotBlank(message = "fullName required")
    private String fullName;

    @NotBlank(message = "countryCode required")
    private String countryCode;

    @NotBlank(message = "phoneNumber required")
    private String phoneNumber;

    @NotBlank(message = "role required")
    private String role; // ADMIN, STUDENT, TRAINER
}
