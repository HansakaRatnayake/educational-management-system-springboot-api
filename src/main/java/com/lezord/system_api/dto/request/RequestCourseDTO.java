package com.lezord.system_api.dto.request;

import com.lezord.system_api.entity.enums.CourseLevel;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestCourseDTO {

    private String name;
    private String description;
    private int duration;
    private int assigmentCount;
    private String introVideoUrl;
    private CourseLevel courseLevel; // VERY_BEGINNER, BEGINNER, INTERMEDIATE, BUSINESS, ADVANCE_NATIVE
    private String prerequisite;
    private MultipartFile courseThumbnail;
}
