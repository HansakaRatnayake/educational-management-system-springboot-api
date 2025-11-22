package com.lezord.system_api.dto.response;

import com.lezord.system_api.entity.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseNonPaidStudentDetailsDTO {

    private ResponseStudentDTO student;
    private String intakeId;
    private String intakeName;
    private String courseName;
    private String courseId;
    private BigDecimal amount;
    private int installmentNumber;
    private LocalDate installmentStartDate;
    private LocalDate installmentEndDate;
    private boolean canAccessCourse;
    private PaymentStatus paymentStatus;

}
