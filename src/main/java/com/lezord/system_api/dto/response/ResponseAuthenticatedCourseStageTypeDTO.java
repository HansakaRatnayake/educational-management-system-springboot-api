package com.lezord.system_api.dto.response;

import lombok.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseAuthenticatedCourseStageTypeDTO {
    private String contentType;
    private AtomicInteger remainingAssignmentCount;
    private List<ResponseAuthnticatedCourseStageDTO> stages;
}