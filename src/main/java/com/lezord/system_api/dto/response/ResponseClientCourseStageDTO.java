package com.lezord.system_api.dto.response;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ResponseClientCourseStageDTO {
    private String title;
    private String description;
    private int orderIndex;
    private List<ResponseClientCourseStageLessonDTO> lessons;
}
