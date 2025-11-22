package com.lezord.system_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "instructor_intake_assignation")
public class InstructorIntakeAssignation {


    @EmbeddedId
    private InstructorIntakeAssignationKey instructorIntakeAssignationKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("instructorId")
    @JoinColumn(name = "instructor_id", referencedColumnName = "property_id")
    private Instructor instructor;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("intakeId")
    @JoinColumn(name = "intake_id", referencedColumnName = "property_id")
    private Intake intake;

    @Column(name = "active_status", nullable = false, columnDefinition = "TINYINT")
    private Boolean activeStatus;

    @Column(name = "progress")
    private double progress;

    @Column(name = "created_date")
    private Instant createdDate;


}
