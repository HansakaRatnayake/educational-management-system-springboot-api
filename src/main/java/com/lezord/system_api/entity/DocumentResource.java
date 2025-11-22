package com.lezord.system_api.entity;


import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "document_resource")
public class DocumentResource {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_record", referencedColumnName ="property_id")
    private LectureRecord lectureRecord;

}
