package com.lezord.system_api.entity;

import com.lezord.system_api.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "student_installment-plan")
public class StudentInstallmentPlan {

    @Id
    @Column(name = "property_id", length = 80)
    private String propertyId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "installment_id", referencedColumnName = "property_id")
    private IntakeInstallment installment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PaymentStatus status;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "next",columnDefinition = "TINYINT")
    private boolean next;

    @OneToOne
    @JoinColumn(name = "payment_id", referencedColumnName = "property_id")
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "property_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intake_id", referencedColumnName = "property_id")
    private Intake intake;

}
