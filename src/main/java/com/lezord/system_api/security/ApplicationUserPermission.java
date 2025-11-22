package com.lezord.system_api.security;

public enum ApplicationUserPermission {
    COURSE_READ("course:read"),
    COURSE_WRITE("course:write"),
    COURSE_REGISTRATION_READ("course_registration:read"),
    COURSE_REGISTRATION_WRITE("course_registration:write"),
    STUDENT_WRITE("student:write"),
    STUDENT_READ("student:read"),
    TRAINER_WRITE("trainer:write"),
    TRAINER_READ("trainer:read"),
    LECTURE_READ("instructor:read"),
    LECTURE_WRITE("instructor:write"),
    EXAM_READ("assigment:read"),
    EXAM_WRITE("assigment:write"),
    ASSIGNMENT_READ("assignment:read"),
    ASSIGNMENT_WRITE("assignment:write"),
    ASSIGNMENT_SUBMISSION_READ("assignment_submission:read"),
    ASSIGNMENT_SUBMISSION_WRITE("assignment_submission:write"),
    PAYMENT_READ("payment:read"),
    PAYMENT_WRITE("payment:write");



    private final String permission;

    ApplicationUserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
