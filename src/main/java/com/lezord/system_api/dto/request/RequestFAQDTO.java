package com.lezord.system_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestFAQDTO {

    @NotBlank(message = "question required")
    private String question;

    @NotBlank(message = "answer required")
    private String answer;
}