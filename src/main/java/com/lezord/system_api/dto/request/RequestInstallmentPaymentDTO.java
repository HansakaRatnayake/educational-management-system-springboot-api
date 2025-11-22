package com.lezord.system_api.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestInstallmentPaymentDTO {
    private String studentId;
    private String intakeId;
    private String installmentId;
}
