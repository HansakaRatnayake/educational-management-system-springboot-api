package com.lezord.system_api.dto.response;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseClientCourseStageTypeDTO {
    private String contentType;
    private List<ResponseClientCourseStageDTO> stages;
}
