
package com.lezord.system_api.service.impl;

import com.lezord.system_api.dto.request.RequestIntakeInstalmentDTO;
import com.lezord.system_api.dto.response.ResponseIntakeInstallmentDTO;
import com.lezord.system_api.entity.Enrollment;
import com.lezord.system_api.entity.Intake;
import com.lezord.system_api.entity.IntakeInstallment;
import com.lezord.system_api.exception.BadRequestException;
import com.lezord.system_api.exception.DuplicateEntryException;
import com.lezord.system_api.exception.EntryNotFoundException;
import com.lezord.system_api.repository.EnrollmentRepository;
import com.lezord.system_api.repository.IntakeInstalmentRepository;
import com.lezord.system_api.repository.IntakeRepository;
import com.lezord.system_api.service.IntakeInstalmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class IntakeInstalmentServiceImpl implements IntakeInstalmentService {

    private final IntakeInstalmentRepository installmentRepo;
    private final IntakeRepository intakeRepo;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public void create(List<RequestIntakeInstalmentDTO> dtos, String intakeId) {


        if (dtos == null || dtos.isEmpty()) throw new BadRequestException("No installment data provided.");

        if (installmentRepo.existsByIntake_PropertyId(intakeId)) throw new DuplicateEntryException("Intake installments already exists.");

        Intake intake = intakeRepo.findById(intakeId)
                .orElseThrow(() -> new EntryNotFoundException("Intake with ID [" + intakeId + "] not found."));

        BigDecimal totalInstallmentAmount = dtos.stream()
                .map(RequestIntakeInstalmentDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        validateTotalAmountDoesNotExceedIntakePrice(totalInstallmentAmount, intake.getPrice());

        List<IntakeInstallment> installments = dtos.stream()
                .map(dto -> mapToIntakeInstallment(dto, intake))
                .toList();

        intake.setInstallmentEnabled(true);
        intakeRepo.save(intake);

        installmentRepo.saveAll(installments);

    }

    @Override
    public void update(List<RequestIntakeInstalmentDTO> dtos, String intakeId) {
        if (dtos == null || dtos.isEmpty()) {
            throw new BadRequestException("No installment data provided.");
        }

        Intake intake = intakeRepo.findById(intakeId)
                .orElseThrow(() -> new EntryNotFoundException("Intake with ID [" + intakeId + "] not found."));

        List<Enrollment> enrollmentByIntake = enrollmentRepository.findEnrollmentByIntake(intake);
        if (!enrollmentByIntake.isEmpty()) throw new BadRequestException("Cannot update installment details. Already have enrollments for this intake");

        if (!intake.isInstallmentEnabled()) {
            throw new BadRequestException("Installments are not enabled for this intake.");
        }

        // Validate total does not exceed intake price
        BigDecimal totalInstallmentAmount = dtos.stream()
                .map(RequestIntakeInstalmentDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        validateTotalAmountDoesNotExceedIntakePrice(totalInstallmentAmount, intake.getPrice());

//        List<IntakeInstallment> updatedInstallments = dtos.stream().map(dto -> {
//            IntakeInstallment existing = installmentRepo.findById(dto.getInstallmentId())
//                    .orElseThrow(() -> new EntryNotFoundException("Installment with ID [" + dto.getInstallmentId() + "] not found."));
//
//            existing.setInstallmentNumber(dto.getInstallmentNumber());
//            existing.setStartDate(dto.getStartDate());
//            existing.setEndDate(dto.getEndDate());
//            existing.setAmount(dto.getAmount());
//            return existing;
//        }).toList();

        installmentRepo.deleteAllByIntake_PropertyId(intakeId);
        installmentRepo.flush(); // force JPA to apply changes to DB
        List<IntakeInstallment> installments = dtos.stream()
                .map(dto -> mapToIntakeInstallment(dto, intake))
                .toList();
        installmentRepo.saveAll(installments);
    }


    @Override
    public void delete(String intakeId) {
        Intake intake = intakeRepo.findById(intakeId).orElseThrow(() -> new EntryNotFoundException("Intake with ID [" + intakeId + "] not found."));

        if (!installmentRepo.existsByIntake_PropertyId(intakeId)) throw new EntryNotFoundException("Installment not found");

        List<Enrollment> enrollmentByIntake = enrollmentRepository.findEnrollmentByIntake(intake);
        if (!enrollmentByIntake.isEmpty()) throw new BadRequestException("Cannot delete installment details. Already have enrollments for this intake");

        installmentRepo.deleteAllByIntake_PropertyId(intakeId);
        intake.setInstallmentEnabled(false);

        intakeRepo.save(intake);
    }

    @Override
    public void changeStatus(String installmentId) {
        IntakeInstallment installment = installmentRepo.findById(installmentId)
                .orElseThrow(() -> new EntryNotFoundException("Installment not found"));

        installment.setStatus(!installment.isStatus());

        installmentRepo.save(installment);

//        List<IntakeInstallment> all = installmentRepo.findAll();
//        LocalDate today = LocalDate.now();
//
//        for (IntakeInstallment installment : all) {
//            boolean isActive = !today.isBefore(installment.getStartDate()) &&
//                    !today.isAfter(installment.getEndDate());
//
//            if (installment.isStatus() != isActive) {
//                installment.setStatus(isActive);
//                installmentRepo.save(installment);
//            }
//        }
    }

    @Override
    public List<ResponseIntakeInstallmentDTO> findInstallmentsByIntake(String intakeId) {
        return installmentRepo.findByIntake_PropertyIdOrderByInstallmentNumberAsc(intakeId).stream()
                .map(IntakeInstalmentServiceImpl::mapToResponseIntakeInstallmentDTO)
                .toList();
    }

    @Override
    public ResponseIntakeInstallmentDTO findInstallmentById(String installmentId) {
        IntakeInstallment installment = installmentRepo.findById(installmentId)
                .orElseThrow(() -> new EntryNotFoundException("Installment not found"));

        return mapToResponseIntakeInstallmentDTO(installment);
    }

    private static IntakeInstallment mapToIntakeInstallment(RequestIntakeInstalmentDTO dto, Intake intake) {
        return IntakeInstallment.builder()
                .propertyId(UUID.randomUUID().toString())
                .installmentNumber(dto.getInstallmentNumber())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .amount(dto.getAmount())
                .status(false) // default to false, updated by scheduler or manual changeStatus()
                .intake(intake)
                .build();
    }

    private static ResponseIntakeInstallmentDTO mapToResponseIntakeInstallmentDTO(IntakeInstallment installment) {
        return ResponseIntakeInstallmentDTO.builder()
                .propertyId(installment.getPropertyId())
                .installmentNumber(installment.getInstallmentNumber())
                .startDate(installment.getStartDate())
                .endDate(installment.getEndDate())
                .amount(installment.getAmount())
                .status(installment.isStatus())
                .intakeId(installment.getIntake().getPropertyId())
                .build();
    }

    private void validateTotalAmountDoesNotExceedIntakePrice(BigDecimal total, BigDecimal price) {
        if (total.compareTo(price) > 0) {
            throw new BadRequestException("Total installment amount (" + total + ") exceeds the intake price (" + price + ").");
        }
    }
}

