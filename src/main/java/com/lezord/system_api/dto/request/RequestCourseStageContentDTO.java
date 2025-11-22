package com.lezord.system_api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestCourseStageContentDTO {


    private String title;
    private String description;
    private String courseStageId;
}
