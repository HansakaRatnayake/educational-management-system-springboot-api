package com.lezord.system_api.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseFailedRequestCustomDTO {
    private String requestId;
    private String fullName;
    private String username;
    private String assignmentName;
    private Boolean requestState;
}
