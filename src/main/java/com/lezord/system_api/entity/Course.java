package com.lezord.system_api.entity;

import com.lezord.system_api.entity.enums.CourseLevel;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "course")
public class Course {

    @Id
    @Column(name = "property_id", length = 75, nullable = false, unique = true)
    private String propertyId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "duration", nullable = false)
    private int duration; // Duration in hours or weeks or months

    @Column(name = "active_status", nullable = false, columnDefinition = "TINYINT")
    private Boolean activeStatus;

    @Column(name = "intro_video_url")
    private String introVideoUrl;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "assigment_count")
    private int assigmentCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "course_level", nullable = false)
    private CourseLevel courseLevel;

    @OneToOne(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private CourseThumbnail courseThumbnail;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<CourseStage> courseStages = new HashSet<>();

    @OneToMany(mappedBy = "course", orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Intake> intakes = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_prerequisite")
    private Course prerequisite;

    @OneToMany(mappedBy = "prerequisite")
    private Set<Course> dependentCourses;

}
