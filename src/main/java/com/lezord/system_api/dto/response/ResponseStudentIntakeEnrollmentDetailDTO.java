package com.lezord.system_api.dto.response;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseStudentIntakeEnrollmentDetailDTO {

    private ResponseIntakeDTO intake;
    private boolean access;
}
