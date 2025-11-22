package com.lezord.system_api.entity;


import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "course_has_prerequisite_course")
public class CoursePrerequisite {

    @EmbeddedId
    private CoursePrerequisiteKey coursePrerequisiteKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("courseId")
    @JoinColumn(name = "course_id", referencedColumnName = "property_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("prerequisiteId")
    @JoinColumn(name = "prerequisite_course_id", referencedColumnName = "property_id")
    private Course prerequisiteCourse;


    @Override
    public String toString() {
        return "CourseHasPrerequisiteCourse{" +
                "courseHasPrerequisiteCourseKey=" + coursePrerequisiteKey +
                ", course=" + course +
                ", prerequisiteCourse=" + prerequisiteCourse +
                '}';
    }
}
