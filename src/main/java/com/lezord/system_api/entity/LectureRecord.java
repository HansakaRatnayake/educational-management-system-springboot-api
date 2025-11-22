package com.lezord.system_api.entity;

import com.lezord.system_api.entity.core.ThumbnailFileResource;
import com.lezord.system_api.entity.core.VideoResource;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lecture_recording")
public class LectureRecord {

    @Id
    @Column(name = "property_id", length = 75, nullable = false, unique = true)
    private String propertyId;

    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate;

    @Column(name = "title")
    private String title;

    @Column(name = "active_state", columnDefinition = "TINYINT", nullable = false)
    private Boolean activeState;

    @Column(name = "free_availability",  columnDefinition = "TINYINT")
    private boolean freeAvailability;

    @Column(length = 16, name = "length")
    private String length;

    @Embedded
    private ThumbnailFileResource thumbnailFileResource;

    @Embedded
    private VideoResource videoResource;

    @Column(name= "download_enabled",  columnDefinition = "TINYINT")
    private boolean downloadEnabled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_stage_conent",referencedColumnName ="property_id")
    private CourseStageContent courseStageContent;

    @OneToMany(mappedBy = "lectureRecord", fetch = FetchType.EAGER)
    private Set<LectureResourceLink> lectureResourceLinks = new HashSet<>();

    @OneToMany(mappedBy = "lectureRecord", fetch = FetchType.EAGER)
    private Set<DocumentResource> documentResources = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intake_id", referencedColumnName = "property_id")
    private Intake intake;


}
