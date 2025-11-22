package com.lezord.system_api.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseStudentAssignmentFailedRequestDTO {
    private String propertyId;
    private Boolean isRequestAccepted;
}
