package com.lezord.system_api.dto.response.converters;

import java.math.BigDecimal;

public interface MonthlyRevenue {
    Integer getYear();
    Integer getMonth();
    BigDecimal getTotalRevenue();
}
