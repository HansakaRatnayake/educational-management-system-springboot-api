package com.lezord.system_api.dto.response;

import lombok.*;

import java.time.Instant;
import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCourseDTO {
    private String propertyId;
    private String name;
    private String description;
    private int duration;
    private Boolean activeStatus;
    private String introVideoUrl;
    private Instant createdAt;
    private Instant updatedAt;
    private String courseLevel;
    private String courseThumbnail;
    private long stagesCount;
    private long lessonsCount;
    private long assignmentCount;
    private ResponsePrerequisitesCourseDTO prerequisite;
    private List<ResponseIntakeDTO> intakes;
    private ResponseIntakeDTO latestIntake;
//    private List<ResponseCourseStageDTO> courseStages;
}
