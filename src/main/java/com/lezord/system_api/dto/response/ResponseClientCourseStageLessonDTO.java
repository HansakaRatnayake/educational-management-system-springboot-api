package com.lezord.system_api.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseClientCourseStageLessonDTO {
    private String propertyId;
    private String title;
    private String description;
    private int orderIndex;
}
