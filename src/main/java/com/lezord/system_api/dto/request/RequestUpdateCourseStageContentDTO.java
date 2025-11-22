package com.lezord.system_api.dto.request;

import com.lezord.system_api.entity.enums.ProgramStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestUpdateCourseStageContentDTO {


    private String title;
    private String description;
    private String courseStageId;
    private ProgramStatus status;
}
