package com.lezord.system_api.dto.response;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseAuthnticatedCourseStageDTO {
    private String title;
    private String description;
    private int orderIndex;
    private List<ResponseAuthenticatedCourseStageLessonDTO> lessons;
}
