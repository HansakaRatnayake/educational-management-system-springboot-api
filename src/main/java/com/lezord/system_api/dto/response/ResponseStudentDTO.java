package com.lezord.system_api.dto.response;

import com.lezord.system_api.entity.enums.Gender;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseStudentDTO {
    private String propertyId;
    private String userId;
    private String displayName;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String nic;
    private String email;
    private Gender gender;
    private String address;
    private String city;
    private String country;
    private boolean activeStatus;
    private ResponseApplicationUserDTO applicationUser;

}
