package com.lezord.system_api.dto.response;


import com.lezord.system_api.entity.enums.CourseLevel;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePrerequisitesCourseDTO {

    private String propertyId;
    private String name;
    private CourseLevel courseLevel;
}
