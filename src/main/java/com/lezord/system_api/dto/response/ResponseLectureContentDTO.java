package com.lezord.system_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseLectureContentDTO {
    private String propertyId;
    private Boolean activeState;
    private Date createdDate;
    private String intakeId;
    private String topic;
}
