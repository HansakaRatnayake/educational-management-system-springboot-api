package com.lezord.system_api.dto.request;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RequestEnrollmentDTO {
    private Boolean isMonthlyPaymentEnabled;
    private BigDecimal amount;
    private Boolean courseCompleteness;
}
