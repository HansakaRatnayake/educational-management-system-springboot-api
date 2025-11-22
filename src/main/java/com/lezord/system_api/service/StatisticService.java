package com.lezord.system_api.service;

import com.lezord.system_api.dto.response.ResponseClientViewBasicStatisticsDTO;

public interface StatisticService {

    ResponseClientViewBasicStatisticsDTO getBasicStatistics();
}
