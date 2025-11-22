package com.lezord.system_api.entity.core;

import com.lezord.system_api.entity.enums.EmploymentType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentDetails {

    @Column(name = "date_joined", length = 20)
    private LocalDate dateJoined;

    @Enumerated(EnumType.STRING)
    @Column(name = "employee_type", length = 20)
    private EmploymentType employmentType; // e.g., FULL_TIME, PART_TIME

    @Column(name = "designation", length = 20)
    private String designation;

    @Column(name = "active_status", length = 20, columnDefinition = "TINYINT")
    private Boolean activeStatus = true;


}
