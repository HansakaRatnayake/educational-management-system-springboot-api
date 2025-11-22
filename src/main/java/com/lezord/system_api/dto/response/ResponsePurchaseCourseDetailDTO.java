package com.lezord.system_api.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePurchaseCourseDetailDTO {

    private String intakeId;
    private String courseId;
    private String courseName;
    private String courseThumbnail;
    private String courseLevel;
    private double price;


}
