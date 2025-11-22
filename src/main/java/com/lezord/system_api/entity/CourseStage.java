package com.lezord.system_api.entity;

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
@Table(name = "course_stage")
public class CourseStage {

    @Id
    @Column(name = "property_id", length = 75, nullable = false, unique = true)
    private String propertyId;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "description", length = 750, columnDefinition = "TEXT")
    private String description;

    @Column(name = "active_status", nullable = false, columnDefinition = "TINYINT")
    private Boolean activeStatus;   

    @Column(name = "created_date")
    private Instant createdDate;

    @Column(name = "updated_date")
    private Instant updatedDate;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;  // Order of stages in the course

    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "property_id", nullable = false)
    private Course course;

    @OneToMany(mappedBy = "courseStage", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CourseStageContent> contentHashSet = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_content_type", referencedColumnName = "property_id")
    private CourseContentType courseContentType;

    @OneToMany(mappedBy = "courseStage", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StudentCourseStageProgress> studentCourseStageProgressHashSet = new HashSet<>();

}
