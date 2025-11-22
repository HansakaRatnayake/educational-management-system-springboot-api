package com.lezord.system_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Arrays;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "course_thumbnail")
public class CourseThumbnail {

    @Id
    @Column(name = "property_id", length = 75, nullable = false, unique = true)
    private String propertyId;


    @Column(name = "created_date", nullable = false, updatable = false)
    private Instant createdDate;


    @Column(name = "updated_date")
    private Instant updatedDate;

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
    @JoinColumn(name = "course_id", referencedColumnName = "property_id")
    private Course course;

    @Override
    public String toString() {
        return "CourseThumbnail{" +
                "propertyId='" + propertyId + '\'' +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                ", directory=" + Arrays.toString(directory) +
                ", fileName=" + Arrays.toString(fileName) +
                ", hash=" + Arrays.toString(hash) +
                ", resourceUrl=" + Arrays.toString(resourceUrl) +
                ", course=" + course +
                '}';
    }
}
