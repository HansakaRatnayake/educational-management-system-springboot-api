package com.lezord.system_api.entity;

import com.lezord.system_api.entity.enums.IntakeStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "intake")
public class Intake {

    @Id
    @Column(name = "property_id", nullable = false, unique = true)
    private String propertyId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "intake_number", nullable = false)
    private int intakeNumber;

    @Column(name = "available_seats", nullable = false)
    private int availableSeats;

    @Column(name = "intake_start_date", nullable = false)
    private LocalDate intakeStartDate;

    @Column(name = "intake_end_date", nullable = false)
    private LocalDate intakeEndDate;

    @Column(name = "active_status", nullable = false)
    private boolean activeStatus;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "installment_enabled", nullable = false)
    private boolean installmentEnabled;

    @OneToMany(mappedBy = "intake", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Enrollment> enrollments = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", referencedColumnName = "property_id")
    private Course course;

    @OneToMany(mappedBy = "intake", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<InstructorIntakeAssignation> instructorIntakeAssignations;

//    @Column(name = "is_expose_to_Client", columnDefinition = "TINYINT")
//    private boolean isExposeToClient;

    @OneToMany(mappedBy = "intake", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<Payment> payments = new HashSet<>();

    @OneToMany(mappedBy = "intake", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<IntakeInstallment> intakeInstallments;


    @OneToMany(mappedBy = "intake",fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LessonAssignment> lessonAssignments;

    @OneToMany(mappedBy = "intake", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LectureRecord> lectureRecords = new HashSet<>();

    @OneToMany(mappedBy = "intake",fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StudentInstallmentPlan> studentInstallmentPlans = new HashSet<>();

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private IntakeStatus status;
}
