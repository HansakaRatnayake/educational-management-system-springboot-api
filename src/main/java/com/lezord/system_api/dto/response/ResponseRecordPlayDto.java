package com.lezord.system_api.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseRecordPlayDto {

    private String id;
    private String title;
    private String duration;
    private String createdAt;
    private String resourceUrl;
    private String thumbnail;
    private String size;
    private String intakeId;
    private String lessonId;
    private boolean downloadEnabled;

}
