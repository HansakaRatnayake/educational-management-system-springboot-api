package com.lezord.system_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestApplicationUserDTO {

    @NotBlank(message = "email required")
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
