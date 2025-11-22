package com.lezord.system_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class InstructorIntakeAssignationKey implements Serializable {

    @Column(name = "instructor_id")
    private String instructorId;

    @Column(name = "intake_id")
    private String intakeId;
}
