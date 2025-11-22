package com.lezord.system_api.service.impl;

import com.lezord.system_api.dto.response.ResponseStudentInstallmentPlanDTO;
import com.lezord.system_api.repository.StudentInstallmentPlanRepository;
import com.lezord.system_api.service.StudentInstallmentPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class StudentInstallmentPlanServiceImpl implements StudentInstallmentPlanService {

    private final StudentInstallmentPlanRepository studentInstallmentPlanRepository;

    @Override
    public List<ResponseStudentInstallmentPlanDTO> getStudentInstallmentPlans(String studentId, String intakeId) {
        return studentInstallmentPlanRepository.findStudentInstallmentPlansByIntake_PropertyIdAndStudent_PropertyId(intakeId, studentId, Sort.by("installment.installmentNumber").ascending())
                .stream()
                .map(
                        studentInstallmentPlan -> ResponseStudentInstallmentPlanDTO.builder()
                                .propertyId(studentInstallmentPlan.getPropertyId())
                                .startDate(studentInstallmentPlan.getInstallment().getStartDate())
                                .endDate(studentInstallmentPlan.getInstallment().getEndDate())
                                .installmentNumber(studentInstallmentPlan.getInstallment().getInstallmentNumber())
                                .paidAt(studentInstallmentPlan.getPaidAt())
                                .status(studentInstallmentPlan.getStatus())
                                .amount(studentInstallmentPlan.getInstallment().getAmount())
                                .next(studentInstallmentPlan.isNext())
                                .orderId(studentInstallmentPlan.getPayment() != null ? studentInstallmentPlan.getPayment().getPropertyId() : null)
                                .build()
                ).toList();
    }
}
