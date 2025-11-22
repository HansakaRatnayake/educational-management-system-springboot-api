package com.lezord.system_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "enrollment")
public class Enrollment {

    @Id
    @Column(name = "property_id", length = 80, nullable = false)
    private String propertyId;

    @Column(name = "created_date", nullable = false)
    private Instant createdDate;

    @Column(name = "active_state", columnDefinition = "TINYINT")
    private Boolean activeState;

    @Column(name = "is_verified", columnDefinition = "TINYINT")
    private Boolean isVerified;

    @Column(name = "can_access_course", columnDefinition = "TINYINT")
    private Boolean canAccessCourse;

    @Column(name = "course_completeness", columnDefinition = "TINYINT")
    private Boolean courseCompleteness;

    @Column(name = "progress")
    private int progress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_property_id", referencedColumnName = "property_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", referencedColumnName = "property_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intake_id", referencedColumnName = "property_id", nullable = false)
    private Intake intake;

}
