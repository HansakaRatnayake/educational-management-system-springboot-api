package com.lezord.system_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RequestLessonAssignmentDTO {
    @NotBlank(message = "title must not be empty")
    private String title;

    @NotBlank(message = "description must not be empty")
    private String description;

    private int time;

    private int passValue;

    private Boolean backwardAvailable;

    private Boolean halfMarksForMultipleAnswers;

    private Boolean finalAssignment;
}
