package com.lezord.system_api.dto.response.util;

import com.lezord.system_api.dto.response.converters.AssignmentMarksStats;

public class EmptyAssignmentMarksStats implements AssignmentMarksStats {
    public Long getMaxMarks() { return 0L; }
    public Long getMinMarks() { return 0L; }
    public Double getAverageMarks() { return 0.0; }
}
