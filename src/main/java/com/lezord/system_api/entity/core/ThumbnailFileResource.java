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
public class ThumbnailFileResource {
    @Lob
    @Column(name = "thumbnail_hash", nullable = false,columnDefinition = "LONGBLOB")
    private Blob thumbnailHash;
    @Lob
    @Column(name = "thumbnail_directory", nullable = false,columnDefinition = "LONGBLOB")
    private Blob thumbnailDirectory;

    @Lob
    @Column(name = "thumbnail_file_name", nullable = false,columnDefinition = "LONGBLOB")
    private Blob thumbnailFileName;

    @Lob
    @Column(name = "thumbnail_resource_url", nullable = false,columnDefinition = "LONGBLOB")
    private Blob thumbnailResourceUrl;
}
