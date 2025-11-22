package com.lezord.system_api.dto.response;

import com.lezord.system_api.entity.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseStudentInstallmentPlanDTO {
    private String propertyId;
    private int installmentNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal amount;
    private Instant paidAt;
    private String orderId;
    private boolean next;
    private PaymentStatus status;


}
