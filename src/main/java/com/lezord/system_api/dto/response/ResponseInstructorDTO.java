package com.lezord.system_api.dto.response;

import com.lezord.system_api.entity.enums.Gender;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseInstructorDTO {

    private String propertyId;

    private String displayName;

    private LocalDate dob;

    private String nic;

    private Gender gender;

    private ResponseAddressDetailDTO address;
    private ResponseEmploymentDetailDTO employment;
    private ResponseAcademicAndProfessionalBackgroundDetailDTO academicAndProfessionalBackground;

    private ResponseApplicationUserDTO applicationUser;

    private List<ResponseInstructorIntakeDTO> assignedIntakes;


}
