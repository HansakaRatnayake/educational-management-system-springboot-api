package com.lezord.system_api.dto.response;

import lombok.*;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseLectureRecordDetailsDTO {
    private String propertyId;

    private Date date;

    private boolean freeAvailability;

    private String title;

    private String length;

    private Boolean activeState;

    private String thumbnailFileResourceUrl;

    private String lectureRecordResourceUrl;

    private boolean downloadEnabled;

    private String intakeId;

    private ResponseCourseStageContentDTO stageContentDTO;

    private List<ResponseLectureResourceLinkDTO> responseLectureResourceLinkDTOS;

}
