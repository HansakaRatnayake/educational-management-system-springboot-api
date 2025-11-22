package com.lezord.system_api.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "student_course_stage_content_progress")
public class StudentCourseStageContentProgress {

    @Id
    @Column(name = "property_id", length = 75, nullable = false, unique = true)
    private String propertyId;

    @Column(name = "is_active", columnDefinition = "TINYINT")
    private Boolean isActive;

    @Column(name = "is_completed", columnDefinition = "TINYINT")
    private Boolean isCompleted;

    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "property_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_stage_contnet_id", referencedColumnName = "property_id")
    private CourseStageContent courseStageContent;
}
