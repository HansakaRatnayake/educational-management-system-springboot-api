package com.lezord.system_api.dto.response;


import com.lezord.system_api.entity.enums.Gender;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseIntakeHasInstructorDTO {

    private String instructorId;
    private String instructorName;
    private String email;
    private LocalDate dob;
    private String nic;
    private Gender gender;
    private ResponseAcademicAndProfessionalBackgroundDetailDTO academicAndProfessionalBackground;
    private ResponseApplicationUserDTO applicationUser;


}
