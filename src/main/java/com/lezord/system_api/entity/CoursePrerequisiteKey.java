package com.lezord.system_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class CoursePrerequisiteKey implements Serializable {

    @Column(name = "course_id")
    private String courseId;

    @Column(name = "prerequisite_id")
    private String prerequisiteId;
}
