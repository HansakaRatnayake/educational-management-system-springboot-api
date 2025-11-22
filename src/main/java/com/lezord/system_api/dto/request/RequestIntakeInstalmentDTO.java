package com.lezord.system_api.dto.request;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestIntakeInstalmentDTO {

    private String installmentId;

    private int installmentNumber;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal amount;


}
