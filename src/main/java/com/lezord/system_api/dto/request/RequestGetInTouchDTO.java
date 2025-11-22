package com.lezord.system_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestGetInTouchDTO {

    @NotBlank(message = "fullName required")
    private String fullName;

    @NotBlank(message = "email required")
    private String email;

    @NotBlank(message = "message required")
    private String message;

    @NotBlank(message = "phone required")
    private String phone;
}
