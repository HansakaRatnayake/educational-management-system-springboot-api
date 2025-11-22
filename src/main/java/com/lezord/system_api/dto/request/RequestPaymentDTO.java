package com.lezord.system_api.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RequestPaymentDTO {
    private String studentId;
    private String intakeId;
    private boolean installmentEnabled;
}
