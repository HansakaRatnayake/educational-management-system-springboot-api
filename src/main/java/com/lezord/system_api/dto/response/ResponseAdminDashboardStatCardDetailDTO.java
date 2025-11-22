package com.lezord.system_api.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseAdminDashboardStatCardDetailDTO {

    private int studentCount;
    private int instructorCount;
    private BigDecimal totalRevenue;
    private int batchCount;
}
