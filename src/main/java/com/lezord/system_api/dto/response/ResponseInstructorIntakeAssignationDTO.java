package com.lezord.system_api.dto.response;


import com.lezord.system_api.entity.enums.Gender;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseInstructorIntakeAssignationDTO {

    private String propertyId;
    private String instructorId;
    private String instructorName;
    private String instructorEmail;
    private LocalDate instructorDob;
    private String instructorNic;
    private Gender instructorGender;
    private ResponseAcademicAndProfessionalBackgroundDetailDTO academicAndProfessionalBackground;
    private ResponseApplicationUserDTO applicationUser;
    private String intakeId;
    private String intakeName;
    private boolean activeStatus;
}
