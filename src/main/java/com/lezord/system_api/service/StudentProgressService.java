package com.lezord.system_api.service;

public interface StudentProgressService {

    boolean verifyEligibilityForNextCourseStage(String studentId);
    int calculateStudentProgress(String studentId, String intakeId);
}
