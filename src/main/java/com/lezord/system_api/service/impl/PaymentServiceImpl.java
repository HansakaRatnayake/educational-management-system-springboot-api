package com.lezord.system_api.service.impl;


import com.lezord.system_api.dto.request.RequestInstallmentPaymentDTO;
import com.lezord.system_api.dto.request.RequestPaymentDTO;

import com.lezord.system_api.entity.*;
import com.lezord.system_api.repository.*;
import com.nozomi.system_api.entity.*;
import com.lezord.system_api.entity.enums.PaymentStatus;
import com.lezord.system_api.exception.BadRequestException;
import com.lezord.system_api.exception.DuplicateEntryException;
import com.lezord.system_api.exception.EntryNotFoundException;
import com.lezord.system_api.exception.InvalidAccessException;
import com.nozomi.system_api.repository.*;
import com.lezord.system_api.service.EmailService;
import com.lezord.system_api.service.EnrollmentService;
import com.lezord.system_api.service.FileService;
import com.lezord.system_api.service.PaymentService;
import com.lezord.system_api.util.FileDataHandler;
import com.lezord.system_api.util.UploadedResourceBinaryDataDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.time.Instant;
import java.util.*;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final IntakeRepository intakeRepository;
    private final StudentRepository studentRepository;
    private final StudentInstallmentPlanRepository installmentPlanRepository;
    private final IntakeInstalmentRepository intakeInstalmentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentService enrollmentService;
    private final EmailService emailService;
    private final FileService fileService;
    private final FileDataHandler fileDataHandler;
    private final PaymentSlipRepo paymentSlipRepo;

    @Value("${aws.bucketName}")
    private String bucket;

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Value("${payhere.merchant_id}")
    private String merchantId;

    @Value("${payhere.merchant_secret}")
    private String merchantSecret;

    @Value("${payhere.return_url}")
    private String returnUrl;

    @Value("${payhere.cancel_url}")
    private String cancelUrl;

    @Value("${payhere.notify_url}")
    private String notifyUrl;

    @Value("${payhere.target_url}")
    private String targetUrl;

    @Value("${client.app.origin}")
    private String clientOrigin;


    @Override
    public String initiatePayment(RequestPaymentDTO requestPaymentDTO) {

        Intake selectedIntake = intakeRepository.findById(requestPaymentDTO.getIntakeId()).orElseThrow(() -> new EntryNotFoundException("intakeId not found"));
        Student selectedStudent = studentRepository.findById(requestPaymentDTO.getStudentId()).orElseThrow(() -> new EntryNotFoundException("studentId not found"));

        if (enrollmentRepository.getEnrollmentByStudentAndIntake(selectedStudent.getPropertyId(), selectedIntake.getPropertyId()).isPresent())
            throw new DuplicateEntryException("You already enrolled to this course");

        System.out.println(requestPaymentDTO.isInstallmentEnabled());
        Payment savedPayment = null;

        if (requestPaymentDTO.isInstallmentEnabled()) {
            if (!installmentPlanRepository.findByInstallment_IntakeAndStudent(selectedIntake, selectedStudent).isEmpty())
                throw new DuplicateEntryException("Installment plan already exists");

            List<IntakeInstallment> definitions = intakeInstalmentRepository.findByIntake_PropertyIdOrderByInstallmentNumberAsc(selectedIntake.getPropertyId());

            if (definitions.isEmpty()) throw new EntryNotFoundException("No installment definitions for this  ");


            IntakeInstallment firstInstallment = definitions.get(0);

            Payment payment = Payment.builder()
                    .propertyId(UUID.randomUUID().toString())
                    .status(PaymentStatus.PENDING)
                    .amount(firstInstallment.getAmount())
                    .installmentNumber(firstInstallment.getInstallmentNumber())
                    .student(selectedStudent)
                    .isInstallmentEnabled(true)
                    .intake(selectedIntake)
                    .paidAt(Instant.now())
                    .build();

            savedPayment = paymentRepository.save(payment);

            List<StudentInstallmentPlan> studentInstallmentPlanList = new ArrayList<>();
            for (IntakeInstallment installment : definitions) {

                studentInstallmentPlanList.add(
                        StudentInstallmentPlan.builder()
                                .propertyId(UUID.randomUUID().toString())
                                .installment(installment)
                                .status(PaymentStatus.PENDING)
                                .payment(installment.getInstallmentNumber() == 1 ? savedPayment : null)
                                .student(selectedStudent)
                                .intake(selectedIntake)
                                .build()
                );
            }
            installmentPlanRepository.saveAll(studentInstallmentPlanList);

        } else {
            Payment payment = Payment.builder()
                    .propertyId(UUID.randomUUID().toString())
                    .status(PaymentStatus.PENDING)
                    .amount(selectedIntake.getPrice())
                    .student(selectedStudent)
                    .isInstallmentEnabled(false)
                    .intake(selectedIntake)
                    .paidAt(Instant.now())
                    .build();

            savedPayment = paymentRepository.save(payment);
        }

        DecimalFormat df = new DecimalFormat("0.00");
        String amountFormatted = df.format(savedPayment.getAmount());

        StringBuilder form = new StringBuilder();
        form
                .append("<html><body>")
                .append("<form id='payhere-form' method='post' action='").append(targetUrl).append("'>")
                .append("<input type='hidden' name='merchant_id' value='").append(merchantId).append("'/>")
                .append("<input type='hidden' name='return_url' value='").append(returnUrl).append("'/>")
                .append("<input type='hidden' name='cancel_url' value='").append(cancelUrl).append("'/>")
                .append("<input type='hidden' name='notify_url' value='").append(notifyUrl).append("'/>")
                .append("<input type='hidden' name='order_id' value='").append(savedPayment.getPropertyId()).append("'/>")
                .append("<input type='hidden' name='items' value='Course Payment'/>")
                .append("<input type='hidden' name='currency' value='LKR'/>")
                .append("<input type='hidden' name='amount' value='").append(amountFormatted).append("'/>")
                .append("<input type='hidden' name='first_name' value='").append(selectedStudent.getFirstName()).append("'/>")
                .append("<input type='hidden' name='last_name' value='").append(selectedStudent.getLastName()).append("'/>")
                .append("<input type='hidden' name='email' value='").append(selectedStudent.getEmail()).append("'/>")
                .append("<input type='hidden' name='phone' value='").append(selectedStudent.getApplicationUser().getPhoneNumber()).append("'/>")
                .append("<input type='hidden' name='address' value='").append(selectedStudent.getAddress()).append("'/>")
                .append("<input type='hidden' name='city' value='").append(selectedStudent.getCity()).append("'/>")
                .append("<input type='hidden' name='country' value='").append(selectedStudent.getCountry()).append("'/>")
                .append("<input type='hidden' name='custom_1' value='").append(selectedStudent.getPropertyId()).append("'/>")
                .append("<input type='hidden' name='custom_2' value='").append(selectedIntake.getPropertyId()).append("'/>")
                .append("<input type='hidden' name='hash' value='").append(
                        getMd5(merchantId + savedPayment.getPropertyId() + amountFormatted + "LKR" + getMd5(merchantSecret))
                ).append("'/>")
                .append("</form>")
                .append("<script> document.getElementById('payhere-form').submit();</script>")
                .append("</body></html>");

        return form.toString();


    }

    @Override
    public void initiatePaymentWithSlip(MultipartFile file, RequestPaymentDTO requestPaymentDTO) {
        if (file.isEmpty()) throw new BadRequestException("File is empty");
        // Upload file first
        UploadedResourceBinaryDataDTO uploadedResourceBinaryDataDTO = fileService.create(file, bucket, "payments/slips");

        Intake selectedIntake = intakeRepository.findById(requestPaymentDTO.getIntakeId())
                .orElseThrow(() -> new EntryNotFoundException("intakeId not found"));

        Student selectedStudent = studentRepository.findById(requestPaymentDTO.getStudentId())
                .orElseThrow(() -> new EntryNotFoundException("studentId not found"));

        if (enrollmentRepository.getEnrollmentByStudentAndIntake(selectedStudent.getPropertyId(), selectedIntake.getPropertyId()).isPresent()) {
            throw new DuplicateEntryException("You already enrolled to this course");
        }

        Payment savedPayment;
        if (requestPaymentDTO.isInstallmentEnabled()) {
            if (!installmentPlanRepository.findByInstallment_IntakeAndStudent(selectedIntake, selectedStudent).isEmpty()) {
                throw new DuplicateEntryException("Installment plan already exists");
            }

            List<IntakeInstallment> definitions = intakeInstalmentRepository
                    .findByIntake_PropertyIdOrderByInstallmentNumberAsc(selectedIntake.getPropertyId());
            if (definitions.isEmpty()) throw new EntryNotFoundException("No installment definitions");

            IntakeInstallment firstInstallment = definitions.get(0);

            Payment payment = Payment.builder()
                    .propertyId(UUID.randomUUID().toString())
                    .status(PaymentStatus.PENDING)
                    .amount(firstInstallment.getAmount())
                    .installmentNumber(firstInstallment.getInstallmentNumber())
                    .student(selectedStudent)
                    .isInstallmentEnabled(true)
                    .intake(selectedIntake)
                    .paidAt(Instant.now())
                    .build();

            savedPayment = paymentRepository.save(payment);

            List<StudentInstallmentPlan> studentInstallments = definitions.stream().map(installment ->
                    StudentInstallmentPlan.builder()
                            .propertyId(UUID.randomUUID().toString())
                            .installment(installment)
                            .status(PaymentStatus.PENDING)
                            .payment(installment.getInstallmentNumber() == 1 ? savedPayment : null)
                            .student(selectedStudent)
                            .intake(selectedIntake)
                            .build()
            ).collect(Collectors.toList());

            installmentPlanRepository.saveAll(studentInstallments);
        } else {
            Payment payment = Payment.builder()
                    .propertyId(UUID.randomUUID().toString())
                    .status(PaymentStatus.PENDING)
                    .amount(selectedIntake.getPrice())
                    .student(selectedStudent)
                    .isInstallmentEnabled(false)
                    .intake(selectedIntake)
                    .paidAt(Instant.now())
                    .build();

            savedPayment = paymentRepository.save(payment);
        }

        PaymentSlip slip = PaymentSlip.builder()
                .propertyId(UUID.randomUUID().toString())
                .createdAt(Instant.now())
                .payment(savedPayment)
                .isVerified(false)
                .hash(fileDataHandler.blobToByteArray(uploadedResourceBinaryDataDTO.getHash()))
                .directory(uploadedResourceBinaryDataDTO.getDirectory().getBytes())
                .fileName(fileDataHandler.blobToByteArray(uploadedResourceBinaryDataDTO.getFilename()))
                .resourceUrl(fileDataHandler.blobToByteArray(uploadedResourceBinaryDataDTO.getResourceUrl()))
                .build();
        paymentSlipRepo.save(slip);
    }

    @Override
    public String initiateInstallmentPayment(RequestInstallmentPaymentDTO requestInstallmentPaymentDTO) {
        Intake selectedIntake = intakeRepository.findById(requestInstallmentPaymentDTO.getIntakeId()).orElseThrow(() -> new EntryNotFoundException("intakeId not found"));
        Student selectedStudent = studentRepository.findById(requestInstallmentPaymentDTO.getStudentId()).orElseThrow(() -> new EntryNotFoundException("studentId not found"));
        StudentInstallmentPlan installmentPlan = installmentPlanRepository.findById(requestInstallmentPaymentDTO.getInstallmentId()).orElseThrow(() -> new EntryNotFoundException("installmentId not found"));

        StudentInstallmentPlan previousInstallment = installmentPlanRepository.findTopByStatusOrderByPaidAtDesc(PaymentStatus.SUCCESS).orElseThrow(() -> new EntryNotFoundException("installmentStatus not found"));

        if (installmentPlan.getInstallment().getInstallmentNumber() - 1 != previousInstallment.getInstallment().getInstallmentNumber())
            throw new BadRequestException(String.format("Please pay for the %s installment before this", (installmentPlan.getInstallment().getInstallmentNumber() - 1)));

        List<IntakeInstallment> definitions = intakeInstalmentRepository.findByIntake_PropertyIdOrderByInstallmentNumberAsc(selectedIntake.getPropertyId());

        if (definitions.isEmpty()) throw new EntryNotFoundException("No installment definitions found");

        IntakeInstallment installment = definitions.get(installmentPlan.getInstallment().getInstallmentNumber() - 1);

        Payment payment = Payment.builder()
                .propertyId(UUID.randomUUID().toString())
                .status(PaymentStatus.PENDING)
                .amount(installment.getAmount())
                .installmentNumber(installment.getInstallmentNumber())
                .student(selectedStudent)
                .isInstallmentEnabled(true)
                .intake(selectedIntake)
                .paidAt(Instant.now())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        installmentPlan.setPayment(savedPayment);
        installmentPlanRepository.save(installmentPlan);

        DecimalFormat df = new DecimalFormat("0.00");
        String amountFormatted = df.format(savedPayment.getAmount());

        StringBuilder form = new StringBuilder();
        form
                .append("<html><body>")
                .append("<form id='payhere-form' method='post' action='").append(targetUrl).append("'>")
                .append("<input type='hidden' name='merchant_id' value='").append(merchantId).append("'/>")
                .append("<input type='hidden' name='return_url' value='").append(returnUrl).append("'/>")
                .append("<input type='hidden' name='cancel_url' value='").append(cancelUrl).append("'/>")
                .append("<input type='hidden' name='notify_url' value='").append(notifyUrl).append("'/>")
                .append("<input type='hidden' name='order_id' value='").append(savedPayment.getPropertyId()).append("'/>")
                .append("<input type='hidden' name='items' value='Course Payment'/>")
                .append("<input type='hidden' name='currency' value='LKR'/>")
                .append("<input type='hidden' name='amount' value='").append(amountFormatted).append("'/>")
                .append("<input type='hidden' name='first_name' value='").append(selectedStudent.getFirstName()).append("'/>")
                .append("<input type='hidden' name='last_name' value='").append(selectedStudent.getLastName()).append("'/>")
                .append("<input type='hidden' name='email' value='").append(selectedStudent.getEmail()).append("'/>")
                .append("<input type='hidden' name='phone' value='").append(selectedStudent.getApplicationUser().getPhoneNumber()).append("'/>")
                .append("<input type='hidden' name='address' value='").append(selectedStudent.getAddress()).append("'/>")
                .append("<input type='hidden' name='city' value='").append(selectedStudent.getCity()).append("'/>")
                .append("<input type='hidden' name='country' value='").append(selectedStudent.getCountry()).append("'/>")
                .append("<input type='hidden' name='custom_1' value='").append(selectedStudent.getPropertyId()).append("'/>")
                .append("<input type='hidden' name='custom_2' value='").append(selectedIntake.getPropertyId()).append("'/>")
                .append("<input type='hidden' name='hash' value='").append(
                        getMd5(merchantId + savedPayment.getPropertyId() + amountFormatted + "LKR" + getMd5(merchantSecret))
                ).append("'/>")
                .append("</form>")
                .append("<script> document.getElementById('payhere-form').submit();</script>")
                .append("</body></html>");

        return form.toString();

    }

    @Override
    public boolean handlePaymentCallback(Map<String, String> payload) {

        String orderId = payload.get("order_id");
        String statusCode = payload.get("status_code");
        String statusmessage = payload.get("status_message");
        String method = payload.get("method");
        String paidAmount = payload.get("payhere_amount");
        String payhereRefId = payload.get("payment_id");
        String student = payload.get("custom_1");
        String intake = payload.get("custom_2");
        String receivedHash = payload.get("md5sig");

        String cardHolderName = payload.get("card_holder_name");
        String cardNo = payload.get("card_no");
        String cardExpiry = payload.get("card_expiry");

        String merchantId = payload.get("merchant_id");
        String currency = payload.get("payhere_currency");

        String localHash = getMd5(merchantId + orderId + paidAmount + currency + statusCode + getMd5(merchantSecret));

        Student selectedStudent = studentRepository.findById(student).orElseThrow(() -> new EntryNotFoundException("Student not found"));
        Intake selectedIntake = intakeRepository.findById(intake).orElseThrow(() -> new EntryNotFoundException("intake not found"));

        if (!localHash.equals(receivedHash)) {
            logger.warn("Invalid payment hash received for order ID: {}", orderId);
            throw new InvalidAccessException("Invalid signature");
        }

        Optional<Payment> selectedPayment = paymentRepository.findById(orderId);

        if (selectedPayment.isEmpty()) return false;

        if (selectedPayment.get().getStatus() != PaymentStatus.PENDING) return false;

        switch (statusCode) {
            case "2":
                selectedPayment.get().setStatus(PaymentStatus.SUCCESS);
                break;

            case "0":
                selectedPayment.get().setStatus(PaymentStatus.PENDING);
                break;

            case "-1":
                selectedPayment.get().setStatus(PaymentStatus.CANCELLED);
                break;

            case "-2":
                selectedPayment.get().setStatus(PaymentStatus.FAILED);
                break;

            case "-3":
                selectedPayment.get().setStatus(PaymentStatus.CHARGED_BACK);
                break;
        }

        selectedPayment.get().setPayHerePaymentId(payhereRefId);
        selectedPayment.get().setCardHolderName(cardHolderName);
        selectedPayment.get().setCardNumber(cardNo);
        selectedPayment.get().setCurrency(currency);
        selectedPayment.get().setMethod(method);
        selectedPayment.get().setExpiryDate(cardExpiry);

        Payment updatedPayment = paymentRepository.save(selectedPayment.get());

        if (updatedPayment.getStatus() == PaymentStatus.SUCCESS) {
            logger.info("payment success : {}", statusmessage);
            if (updatedPayment.isInstallmentEnabled()) {
                StudentInstallmentPlan installmentPlan = installmentPlanRepository.findStudentInstallmentPlanByPayment(updatedPayment.getPropertyId()).orElseThrow(() -> new EntryNotFoundException("Student installment plan not found"));
                installmentPlan.setPaidAt(Instant.now());
                installmentPlan.setStatus(PaymentStatus.SUCCESS);
                installmentPlan.setNext(false);
                installmentPlanRepository.save(installmentPlan);

                StudentInstallmentPlan nextInstallment = installmentPlanRepository.findByInstallment_InstallmentNumberAndStudent_PropertyIdAndIntake_PropertyId(updatedPayment.getInstallmentNumber() + 1, student, intake).orElse(null);
                if (nextInstallment != null) {
                    nextInstallment.setNext(true);
                    installmentPlanRepository.save(nextInstallment);
                }
            }
            if (updatedPayment.isInstallmentEnabled() && updatedPayment.getStudentInstallmentPlan().getInstallment().getInstallmentNumber() > 1) {
                Enrollment enrollment = enrollmentRepository.findByStudentPropertyIdAndCoursePropertyId(student, selectedIntake.getCourse().getPropertyId()).orElseThrow(() -> new EntryNotFoundException("Enrollment cannot found"));

                enrollment.setCanAccessCourse(true);
                enrollmentRepository.save(enrollment);
                emailService.sendInstallmentPaymentSuccessEmail(
                        selectedStudent.getEmail(),
                        "Installment Payment Successful",
                        selectedIntake.getCourse().getName(),
                        updatedPayment.getAmount(),
                        updatedPayment.getPaidAt().toString(),
                        updatedPayment.getMethod(),
                        updatedPayment.getStudentInstallmentPlan().getInstallment().getEndDate().toString(),
                        updatedPayment.getInstallmentNumber(),
                        clientOrigin + "/lms/process/my-courses/details?courseId=" + selectedIntake.getCourse().getPropertyId(),
                        updatedPayment.getPropertyId()
                );
                return true;
            }
            if (enrollmentService.createEnrollment(student, intake)) {
                System.out.println("created");
                emailService.sendCourseEnrollmentSuccessEmail(
                        selectedStudent.getEmail(),
                        "Course Enrollment Successful",
                        selectedIntake.getCourse().getName(),
                        selectedIntake.getIntakeStartDate().toString(),
                        String.valueOf(selectedIntake.getCourse().getDuration())
                );
                return true;
            }
        }
        logger.error("Invalid payment : {}", statusmessage);
        return false;
    }

    @Override
    public boolean handlePaymentSlipData(String paymentId) {

        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new EntryNotFoundException("Payment not found"));

        Student selectedStudent = studentRepository.findById(payment.getStudent().getPropertyId()).orElseThrow(() -> new EntryNotFoundException("Student not found"));
        Intake selectedIntake = intakeRepository.findById(payment.getIntake().getPropertyId()).orElseThrow(() -> new EntryNotFoundException("intake not found"));


        if (payment.getStatus() != PaymentStatus.PENDING) return false;

        payment.setStatus(PaymentStatus.SUCCESS);

        payment = paymentRepository.save(payment);

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            if(payment.getPaymentSlips().get(0)!=null){
                Optional<PaymentSlip> byId = paymentSlipRepo.findById(payment.getPaymentSlips().get(0).getPropertyId());
                if(byId.isPresent()){
                    byId.get().setVerified(true);
                    paymentSlipRepo.save(byId.get());
                }
            }


            if (payment.isInstallmentEnabled()) {
                StudentInstallmentPlan installmentPlan = installmentPlanRepository.findStudentInstallmentPlanByPayment(payment.getPropertyId()).orElseThrow(() -> new EntryNotFoundException("Student installment plan not found"));
                installmentPlan.setPaidAt(Instant.now());
                installmentPlan.setStatus(PaymentStatus.SUCCESS);
                installmentPlan.setNext(false);
                installmentPlanRepository.save(installmentPlan);

                StudentInstallmentPlan nextInstallment = installmentPlanRepository.findByInstallment_InstallmentNumberAndStudent_PropertyIdAndIntake_PropertyId(payment.getInstallmentNumber() + 1, payment.getStudent().getPropertyId(), payment.getIntake().getPropertyId()).orElse(null);
                if (nextInstallment != null) {
                    nextInstallment.setNext(true);
                    installmentPlanRepository.save(nextInstallment);
                }
            }
            if (payment.isInstallmentEnabled() && payment.getStudentInstallmentPlan().getInstallment().getInstallmentNumber() > 1) {
                Enrollment enrollment = enrollmentRepository.findByStudentPropertyIdAndCoursePropertyId(payment.getStudent().getPropertyId(), selectedIntake.getCourse().getPropertyId()).orElseThrow(() -> new EntryNotFoundException("Enrollment cannot found"));

                enrollment.setCanAccessCourse(true);
                enrollmentRepository.save(enrollment);
                emailService.sendInstallmentPaymentSuccessEmail(
                        selectedStudent.getEmail(),
                        "Installment Payment Successful",
                        selectedIntake.getCourse().getName(),
                        payment.getAmount(),
                        payment.getPaidAt().toString(),
                        payment.getMethod(),
                        payment.getStudentInstallmentPlan().getInstallment().getEndDate().toString(),
                        payment.getInstallmentNumber(),
                        clientOrigin + "/lms/process/my-courses/details?courseId=" + selectedIntake.getCourse().getPropertyId(),
                        payment.getPropertyId()
                );
                return true;
            }
            if (enrollmentService.createEnrollment(payment.getStudent().getPropertyId(), payment.getIntake().getPropertyId())) {
                System.out.println("created");
                emailService.sendCourseEnrollmentSuccessEmail(
                        selectedStudent.getEmail(),
                        "Course Enrollment Successful",
                        selectedIntake.getCourse().getName(),
                        selectedIntake.getIntakeStartDate().toString(),
                        String.valueOf(selectedIntake.getCourse().getDuration())
                );
                return true;
            }
        }
        return false;
    }

    private static String getMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext.toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
//Mzg5MzExNjQ1MTIzOTkxOTEwNTkxODg3MzIyMjUwMjIyMTA3Njc4Mg==
//www.nozomi-e-learning.com


