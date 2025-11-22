package com.lezord.system_api.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseIntakeInstallmentDTO {

    private String propertyId;

    private int installmentNumber;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal amount;

    private String intakeId;

    private boolean status;

}
