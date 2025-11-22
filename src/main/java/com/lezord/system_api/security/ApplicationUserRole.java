package com.lezord.system_api.security;

import com.google.common.collect.Sets;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public enum ApplicationUserRole {

    ADMIN(Sets.newHashSet(
            ApplicationUserPermission.COURSE_READ,
            ApplicationUserPermission.COURSE_WRITE,
            ApplicationUserPermission.COURSE_REGISTRATION_WRITE,
            ApplicationUserPermission.COURSE_REGISTRATION_READ,
            ApplicationUserPermission.TRAINER_READ,
            ApplicationUserPermission.TRAINER_WRITE,
            ApplicationUserPermission.STUDENT_WRITE,
            ApplicationUserPermission.STUDENT_READ,
            ApplicationUserPermission.EXAM_READ,
            ApplicationUserPermission.EXAM_WRITE,
            ApplicationUserPermission.ASSIGNMENT_READ,
            ApplicationUserPermission.ASSIGNMENT_WRITE,
            ApplicationUserPermission.LECTURE_WRITE,
            ApplicationUserPermission.LECTURE_READ,
            ApplicationUserPermission.PAYMENT_READ,
            ApplicationUserPermission.PAYMENT_WRITE
    )),
    STUDENT(Sets.newHashSet(
            ApplicationUserPermission.COURSE_READ,
            ApplicationUserPermission.LECTURE_READ,
            ApplicationUserPermission.TRAINER_READ,
            ApplicationUserPermission.EXAM_READ,
            ApplicationUserPermission.ASSIGNMENT_READ,
            ApplicationUserPermission.ASSIGNMENT_SUBMISSION_WRITE,
            ApplicationUserPermission.COURSE_REGISTRATION_READ
    )),
    TRAINER(Sets.newHashSet(
            ApplicationUserPermission.COURSE_READ,
            ApplicationUserPermission.COURSE_WRITE,
            ApplicationUserPermission.STUDENT_READ,
            ApplicationUserPermission.LECTURE_WRITE,
            ApplicationUserPermission.LECTURE_READ,
            ApplicationUserPermission.EXAM_WRITE,
            ApplicationUserPermission.EXAM_READ,
            ApplicationUserPermission.ASSIGNMENT_WRITE,
            ApplicationUserPermission.ASSIGNMENT_READ,
            ApplicationUserPermission.ASSIGNMENT_SUBMISSION_READ
    ));

    private final Set<ApplicationUserPermission> applicationUserPermissions;

    ApplicationUserRole(Set<ApplicationUserPermission> applicationUserPermissions) {
        this.applicationUserPermissions = applicationUserPermissions;
    }


    public Set<ApplicationUserPermission> getApplicationUserPermissions() {
        return applicationUserPermissions;
    }

    public Set<SimpleGrantedAuthority> grantedAuthorities() {
        Set<SimpleGrantedAuthority> permissions = getApplicationUserPermissions()
                .stream().map(permission ->
                        new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        permissions.add(
                new SimpleGrantedAuthority("ROLE_" + this.name())
        );

        return permissions;
    }
}
