package com.lezord.system_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Blob;
import java.time.Instant;


@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lecture_resource_link")
public class LectureResourceLink {
    @Id
    @Column(name = "property_id", nullable = false, length = 80)
    private String propertyId;

    @Column(name = "active_state", columnDefinition = "TINYINT", nullable = false)
    private Boolean activeState;

    @Lob
    @Column(name = "resource_url", nullable = false)
    private Blob resourceUrl;

    @Column(name = "resource_date")
    private Instant resourceDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_property_id", referencedColumnName = "property_id", nullable = false)
    private LectureRecord lectureRecord;
}
