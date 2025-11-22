package com.lezord.system_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestCourseStageDTO {

    @NotBlank(message = "Title required")
    private String title;

    private String description;

    @NotBlank(message = "CourseId required")
    private String courseId;

    @NotBlank(message = "CourseContentType required")
    private String courseContentType;
}
