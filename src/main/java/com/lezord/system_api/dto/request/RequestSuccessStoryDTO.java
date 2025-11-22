package com.lezord.system_api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestSuccessStoryDTO {

    @NotNull(message = "title required")
    private String title;

    @Min(0)
    @NotNull(message = "rating required")
    private int rating;

    @NotBlank(message = "story required")
    private String story;

    @NotBlank(message = "userId required")
    private String userId;

}

