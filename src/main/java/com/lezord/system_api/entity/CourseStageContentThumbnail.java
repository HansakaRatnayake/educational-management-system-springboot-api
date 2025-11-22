package com.lezord.system_api.entity;

import jakarta.persistence.*;
import lombok.*;


@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "course_stage_thumbnail")
public class CourseStageContentThumbnail {

    @Id
    @Column(name = "property_id", length = 75, nullable = false, unique = true)
    private String propertyId;

    @Column(name = "created_date", nullable = false)
    private String createdDate;

    @Lob
    @Column(name = "directory", nullable = false,columnDefinition = "LONGBLOB")
    private byte[] directory;

    @Lob
    @Column(name = "file_name", nullable = false,columnDefinition = "LONGBLOB")
    private byte[] fileName;

    @Lob
    @Column(name = "hash", nullable = false,columnDefinition = "LONGBLOB")
    private byte[] hash;

    @Lob
    @Column(name = "resource_url",columnDefinition = "LONGBLOB")
    private byte[] resourceUrl;

    @OneToOne
    @JoinColumn(name = "course_stage_content_id", referencedColumnName = "property_id")
    private CourseStageContent courseStageContent;
}
