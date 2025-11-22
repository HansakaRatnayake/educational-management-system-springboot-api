package com.lezord.system_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "intake_installment")
public class IntakeInstallment {

    @Id
    @Column(name = "property_id", length = 80)
    private String propertyId;

    @Column(name = "installment_number", nullable = false)
    private int installmentNumber;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "status", columnDefinition = "TINYINT")
    private boolean status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intake_id", referencedColumnName = "property_id", nullable = true)
    private Intake intake;

    @OneToMany(mappedBy = "installment", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<StudentInstallmentPlan> installmentPlans;
}