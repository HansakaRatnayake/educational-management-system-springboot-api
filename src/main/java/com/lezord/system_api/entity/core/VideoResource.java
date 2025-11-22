package com.lezord.system_api.entity.core;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Blob;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VideoResource {
    @Lob
    @Column(name = "video_hash", nullable = false,columnDefinition = "LONGBLOB")
    private Blob videoHash;
    @Lob
    @Column(name = "video_directory", nullable = false,columnDefinition = "LONGBLOB")
    private Blob videoDirectory;

    @Lob
    @Column(name = "video_file_name", nullable = false,columnDefinition = "LONGBLOB")
    private Blob videoFileName;

    @Lob
    @Column(name = "video_resource_url", nullable = false,columnDefinition = "LONGBLOB")
    private Blob videoResourceUrl;

    @Column(name = "size", nullable = false)
    private Long size;
}
