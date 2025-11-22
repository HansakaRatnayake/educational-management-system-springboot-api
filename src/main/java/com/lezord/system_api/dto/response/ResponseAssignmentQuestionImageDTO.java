package com.lezord.system_api.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseAssignmentQuestionImageDTO {
    private String propertyId;
    private String resourceUrl;
}
