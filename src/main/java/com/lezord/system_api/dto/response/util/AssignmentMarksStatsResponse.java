package com.lezord.system_api.dto.response.util;

import com.lezord.system_api.dto.response.converters.AssignmentMarksStats;

public class AssignmentMarksStatsResponse {
    private Long maxMarks;
    private Long minMarks;
    private Double averageMarks;

    public AssignmentMarksStatsResponse(AssignmentMarksStats stats) {
        this.maxMarks = stats.getMaxMarks() != null ? stats.getMaxMarks() : 0L;
        this.minMarks = stats.getMinMarks() != null ? stats.getMinMarks() : 0L;
        this.averageMarks = stats.getAverageMarks() != null ? stats.getAverageMarks() : 0.0;
    }

    // Getters and optionally setters
    public Long getMaxMarks() {
        return maxMarks;
    }

    public Long getMinMarks() {
        return minMarks;
    }

    public Double getAverageMarks() {
        return averageMarks;
    }
}
