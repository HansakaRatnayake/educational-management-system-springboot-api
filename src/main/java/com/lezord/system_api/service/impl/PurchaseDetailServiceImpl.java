package com.lezord.system_api.service.impl;

import com.lezord.system_api.dto.response.*;
import com.lezord.system_api.entity.*;
import com.lezord.system_api.repository.EnrollmentRepository;
import com.lezord.system_api.repository.PaymentRepository;
import com.lezord.system_api.repository.StudentRepository;
import com.nozomi.system_api.dto.response.*;
import com.lezord.system_api.dto.response.paginate.PaginatePurchaseDetailDTO;
import com.nozomi.system_api.entity.*;
import com.lezord.system_api.exception.EntryNotFoundException;
import com.nozomi.system_api.repository.*;
import com.lezord.system_api.service.PurchaseDetailService;
import com.lezord.system_api.service.StudentInstallmentPlanService;
import com.lezord.system_api.util.FileDataHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseDetailServiceImpl implements PurchaseDetailService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentInstallmentPlanService studentInstallmentPlanService;
    private final FileDataHandler fileDataHandler;
    private final StudentRepository studentRepository;
    private final PaymentRepository paymentRepository;


    @Override
    public List<ResponsePurchaseDetailDTO> getPurchaseDetails(String studentId) {
        studentRepository.findById(studentId).orElseThrow(() -> new EntryNotFoundException("Student not found"));
        List<Enrollment> enrollmentList = enrollmentRepository.findAllByStudent_PropertyId(studentId);

        List<ResponsePurchaseDetailDTO> purchaseDetailDTOS = new ArrayList<>();

        for (Enrollment enrollment : enrollmentList) {
            Course course = enrollment.getCourse();
            Intake intake = enrollment.getIntake();
            Student student = enrollment.getStudent();


            // Fetch latest payment for this student & intake
            Payment payment = intake.getPayments().stream()
                    .filter(p -> p.getStudent().getPropertyId().equals(student.getPropertyId()))
                    .findFirst()
                    .orElseThrow(() -> new EntryNotFoundException("Payment not found"));

            // Map to Response DTOs
            ResponsePurchaseCourseDetailDTO courseDetailDTO = mapCourseToDTO(course, intake);
            ResponsePurchaseOrderPaymentDetailDTO orderPaymentDetailDTO = mapPaymentToDTO(payment);

            purchaseDetailDTOS.add(ResponsePurchaseDetailDTO.builder()
                    .courseDetailDTO(courseDetailDTO)
                    .orderPaymentDetailDTO(orderPaymentDetailDTO)
                    .paymentSlip(
                            !payment.getPaymentSlips().isEmpty() ? fileDataHandler.byteArrayToString(payment.getPaymentSlips().get(0).getResourceUrl()) : "")
                    .build());
        }

        return purchaseDetailDTOS;
    }

    @Override
    public PaginatePurchaseDetailDTO getAllPendingSlipVerifiedData(String searchText, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Payment> paymentPage = paymentRepository.findUnverifiedPaymentSlipsByStudentInfo(searchText, pageable);

        List<ResponsePurchaseDetailDTO> details = new ArrayList<>();

        for (Payment payment : paymentPage.getContent()) {
            Student student = payment.getStudent();
            Intake intake = payment.getIntake();
            Course course = intake.getCourse();

            ResponsePurchaseCourseDetailDTO courseDetailDTO = mapCourseToDTO(course, intake);
            ResponsePurchaseOrderPaymentDetailDTO orderPaymentDetailDTO = mapPaymentToDTO(payment);
            ResponseStudentDTO studentDTO = mapStudentToDTO(student);

            String slipUrl = "";
            if (payment.getPaymentSlips() != null && !payment.getPaymentSlips().isEmpty()) {
                PaymentSlip slip = payment.getPaymentSlips().stream()
                        .filter(s -> !s.isVerified())
                        .findFirst()
                        .orElse(null);

                if (slip != null) slipUrl = fileDataHandler.byteArrayToString(slip.getResourceUrl());
            }

            details.add(ResponsePurchaseDetailDTO.builder()
                    .courseDetailDTO(courseDetailDTO)
                    .orderPaymentDetailDTO(orderPaymentDetailDTO)
                    .responseStudentDTO(studentDTO)
                    .paymentSlip(slipUrl)
                    .build());
        }

        return PaginatePurchaseDetailDTO.builder()
                .dataList(details)
                .count(paymentRepository.countUnverifiedPaymentSlips(searchText))
                .build();
    }


    private ResponsePurchaseCourseDetailDTO mapCourseToDTO(Course course, Intake intake) {
        return ResponsePurchaseCourseDetailDTO.builder()
                .courseId(course.getPropertyId())
                .intakeId(intake.getPropertyId())
                .courseName(course.getName())
                .courseLevel(course.getCourseLevel().name())
                .courseThumbnail(course.getCourseThumbnail() != null ? fileDataHandler.byteArrayToString(course.getCourseThumbnail().getResourceUrl()) : null)
                .price(intake.getPrice().doubleValue())
                .build();
    }

    private ResponsePurchaseOrderPaymentDetailDTO mapPaymentToDTO(Payment payment) {
        List<ResponseStudentInstallmentPlanDTO> sip = studentInstallmentPlanService.getStudentInstallmentPlans(payment.getStudent().getPropertyId(), payment.getIntake().getPropertyId());


        return ResponsePurchaseOrderPaymentDetailDTO.builder()
                .paidAt(payment.getPaidAt() != null ? payment.getPaidAt().toString() : null)
                .installmentEnabled(payment.isInstallmentEnabled())
                .method(payment.getMethod())
                .cardHolderName(payment.getCardHolderName())
                .cardNumber(payment.getCardNumber())
                .cardExpiryDate(payment.getExpiryDate())
                .orderId(payment.getPropertyId())
                .status(payment.getStatus().name())
                .studentInstallmentPlanDTO(sip)
                .build();

    }

    private ResponseStudentDTO mapStudentToDTO(Student student) {
        return ResponseStudentDTO.builder()
                .propertyId(student.getPropertyId())
                .displayName(student.getDisplayName())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .dob(student.getDob())
                .nic(student.getNic())
                .email(student.getEmail())
                .gender(student.getGender())
                .address(student.getAddress())
                .city(student.getCity())
                .country(student.getCountry())
                .activeStatus(Boolean.TRUE.equals(student.getActiveState()))
                .build();
    }
}
