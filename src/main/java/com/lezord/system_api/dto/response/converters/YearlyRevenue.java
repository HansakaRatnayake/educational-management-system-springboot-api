package com.lezord.system_api.dto.response.converters;

import java.math.BigDecimal;

public interface YearlyRevenue {
    Integer getYear();
    BigDecimal getTotalRevenue();
}
