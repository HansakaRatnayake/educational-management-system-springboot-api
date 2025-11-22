package com.lezord.system_api.dto.response;

import lombok.*;

import java.time.Instant;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCourseStageDTO {
    private String propertyId;
    private String title;
    private String description;
    private Instant createdDate;
    private Instant updatedDate;
    private int orderIndex;

}
