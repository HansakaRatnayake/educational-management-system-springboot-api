package com.lezord.system_api.dto.response;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCourseStageContentDTO {

    private String propertyId;

    private String title;

    private String description;

    private Boolean activeStatus;

    private Instant createdDate;

    private int orderIndex;

    private String status;
}
