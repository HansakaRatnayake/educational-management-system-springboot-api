package com.lezord.system_api.dto.response;

import com.lezord.system_api.entity.enums.Gender;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseAdminDTO {

    private String propertyId;
    private String userId;
    private String displayName;
    private LocalDate dob;
    private String nic;
    private String email;
    private Gender gender;
    private boolean activeStatus;
    private ResponseAddressDetailDTO address;
    private ResponseEmploymentDetailDTO employment;
    private ResponseApplicationUserDTO applicationUser;
}
