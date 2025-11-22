package com.lezord.system_api.entity;

import com.lezord.system_api.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment")
public class Payment {

    @Id
    @Column(name = "property_id", length = 80)
    private String propertyId;

    private String payHerePaymentId;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private Instant paidAt;

    private boolean isInstallmentEnabled;

    private int installmentNumber;

    private String cardHolderName;

    private String cardNumber;

    private String expiryDate;

    private String currency;

    private String method;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intake_id", referencedColumnName = "property_id")
    private Intake intake;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "property_id")
    private Student student;

    @OneToOne(mappedBy = "payment", fetch = FetchType.EAGER)
    private StudentInstallmentPlan studentInstallmentPlan;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentSlip> paymentSlips = new ArrayList<>();
}
