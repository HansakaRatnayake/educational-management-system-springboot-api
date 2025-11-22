package com.lezord.system_api.entity;

import com.lezord.system_api.entity.enums.ProgramStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "course_stage_content")
public class CourseStageContent {

    @Id
    @Column(name = "property_id", length = 75, nullable = false, unique = true)
    private String propertyId;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "description", length = 750, nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "active_status", nullable = false, columnDefinition = "TINYINT")
    private Boolean activeStatus;

    @Column(name = "created_date")
    private Instant createdDate;

    @Column(name = "updated_date")
    private Instant updatedDate;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProgramStatus status;

    @ManyToOne
    @JoinColumn(name = "course_stage_id", nullable = false)
    private CourseStage courseStage;

    @OneToMany(mappedBy = "lesson", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LessonAssignment> lessonAssignments;

    @OneToMany(mappedBy = "courseStageContent", fetch = FetchType.EAGER)
    private Set<LectureRecord> lectureRecords = new HashSet<>();
}
