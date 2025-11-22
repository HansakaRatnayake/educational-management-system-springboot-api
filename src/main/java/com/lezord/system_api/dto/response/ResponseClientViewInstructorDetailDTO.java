package com.lezord.system_api.dto.response;


import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseClientViewInstructorDetailDTO {

    String propertyId;
    String instructorName;
    String email;
    int experience;
    String highestQualification;
    String bio;
    String avatar;
    List<ResponseIntakeDTO> assignedIntakes;


}
