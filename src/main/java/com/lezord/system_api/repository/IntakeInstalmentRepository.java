package com.lezord.system_api.repository;


import com.lezord.system_api.entity.IntakeInstallment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface IntakeInstalmentRepository extends JpaRepository<IntakeInstallment, String> {
    List<IntakeInstallment> findByIntake_PropertyIdOrderByInstallmentNumberAsc(String intakePropertyId);


    @Query("SELECT COALESCE(SUM(ii.amount), 0) FROM IntakeInstallment ii WHERE ii.intake.propertyId = :intakeId")
    BigDecimal getTotalAmountByIntakeId(@Param("intakeId") String intakeId);

    boolean existsByIntake_PropertyId(String intakePropertyId);

    void deleteAllByIntake_PropertyId(String intakePropertyId);
}
