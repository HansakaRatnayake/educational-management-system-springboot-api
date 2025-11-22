package com.lezord.system_api.util;

import lombok.*;

import java.sql.Blob;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadedResourceBinaryDataDTO {

    private Blob hash;
    private String directory;
    private Blob filename;
    private Blob resourceUrl;
    private Long size;

}
