package com.lezord.system_api.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseStudentDashboardAssigmentMarksDetailDTO {

    private String assigmentType;
    private Long marks;
//    private ArrayList<Long> marks;
//    private ArrayList<String> name;

}
