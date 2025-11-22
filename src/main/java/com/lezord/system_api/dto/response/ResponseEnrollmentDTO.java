package com.lezord.system_api.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseEnrollmentDTO {
    private String propertyId;

    private Instant createdDate;

    private Boolean activeState;

    private Boolean isMonthlyPaymentEnabled;

    private Boolean isVerified;

    private BigDecimal amount;

    private Boolean courseCompleteness;

    private ResponseStudentDTO student;

    private ResponseCourseDTO course;

    private ResponseIntakeDTO intake;
}
