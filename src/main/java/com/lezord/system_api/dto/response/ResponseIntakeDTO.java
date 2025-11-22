package com.lezord.system_api.dto.response;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseIntakeDTO {

    private String propertyId;

    private String name;

    private int intakeNumber;

    private LocalDate intakeStartDate;

    private LocalDate intakeEndDate;

    private int availableSeats;

    private boolean activeStatus;

    private BigDecimal price;

    private boolean isInstalmentEnabled;

    private String courseId;

    private String courseName;

    private List<ResponseIntakeHasInstructorDTO> instructors;



}
