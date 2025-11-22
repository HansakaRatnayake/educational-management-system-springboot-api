package com.lezord.system_api.dto.response;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePendingInstructorRegistrationDetailDTO {

    private String propertyId;

    private String username;

    private String fullName;

    private String countryCode;

    private String phoneNumber;

    private Instant requestDate;

}
