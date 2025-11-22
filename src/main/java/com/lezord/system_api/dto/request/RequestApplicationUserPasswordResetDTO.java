package com.lezord.system_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestApplicationUserPasswordResetDTO {

    @NotBlank(message = "email required")
    private String email;

    @NotBlank(message = "code required")
    private String code;

    @NotBlank(message = "password required")
    private String password;
}
