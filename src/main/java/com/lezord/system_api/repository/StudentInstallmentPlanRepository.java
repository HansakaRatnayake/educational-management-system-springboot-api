package com.lezord.system_api.repository;

import com.lezord.system_api.entity.Intake;
import com.lezord.system_api.entity.Student;
import com.nozomi.system_api.entity.*;
import com.lezord.system_api.entity.StudentInstallmentPlan;
import com.lezord.system_api.entity.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentInstallmentPlanRepository extends JpaRepository<StudentInstallmentPlan, String> {


    List<StudentInstallmentPlan> findByInstallment_IntakeAndStudent(Intake installmentIntake, Student student);

    @Query(
            "select s from StudentInstallmentPlan s where s.payment.propertyId = :paymentId"
    )
    Optional<StudentInstallmentPlan> findStudentInstallmentPlanByPayment(@Param("paymentId") String paymentId);

    List<StudentInstallmentPlan> findStudentInstallmentPlansByPayment_Student_PropertyIdAndInstallment_Intake_PropertyId(String paymentStudentPropertyId, String installmentIntakePropertyId);

    List<StudentInstallmentPlan> findStudentInstallmentPlansByIntake_PropertyIdAndStudent_PropertyId(String intakePropertyId, String studentPropertyId, Sort sort);

    Optional<StudentInstallmentPlan> findTopByStatusOrderByPaidAtDesc(PaymentStatus status);

    Optional<StudentInstallmentPlan> findByInstallment_InstallmentNumberAndStudent_PropertyIdAndIntake_PropertyId(int installmentInstallmentNumber, String studentPropertyId, String intakePropertyId);


    @Query("""
            select s from StudentInstallmentPlan s where (s.status = :status and s.next = :next and s.installment.endDate < :installmentEndDateBefore) and s.student.email LIKE concat('%',:searchText,'%')
           """
    )
    Page<StudentInstallmentPlan> findStudentInstallmentPlansByStatusAndNextAndInstallmentEndDateBefore(PaymentStatus status, boolean next, LocalDate installmentEndDateBefore,String searchText, Pageable pageable);



    @Query("""
            select s from StudentInstallmentPlan s where (s.status = :status and s.next = :next and s.installment.endDate < :installmentEndDateBefore)
           """
    )
    List<StudentInstallmentPlan> findStudentInstallmentPlansByStatusAndNextAndInstallmentEndDateBefore(PaymentStatus status, boolean next, LocalDate installmentEndDateBefore);

}
