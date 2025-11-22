package com.lezord.system_api.entity;

import com.lezord.system_api.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "student")
public class Student {

    @Id
    @Column(name = "property_id", length = 75, nullable = false, unique = true)
    private String propertyId;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "display_name", length = 100)
    private String displayName;

    @Column(name = "active_state", columnDefinition = "TINYINT", nullable = false)
    private Boolean activeState = true;

    @Column(name = "dob", columnDefinition = "DATE")
    private LocalDate dob;

    @Column(name = "nic", length = 20)
    private String nic;

    @Column(name = "email", length = 100)
    private String email;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private ApplicationUser applicationUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 45)
    private Gender gender;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "country", length = 100)
    private String country;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private Set<Enrollment> enrollmentHashSet= new HashSet<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StudentCourseStageProgress> studentCourseStageProgressHashSet = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "student")
    private Set<Payment> payments = new HashSet<>();

    @OneToMany(mappedBy = "student",fetch = FetchType.LAZY)
    private Set<LessonAssignmentTemp> assignmentTemps;

    @OneToMany(mappedBy = "student",fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StudentHasAssignment> studentHasAssignments;

    @OneToMany(mappedBy = "student",fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StudentInstallmentPlan> studentInstallmentPlans = new HashSet<>();

}
