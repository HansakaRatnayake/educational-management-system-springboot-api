package com.lezord.system_api.service;


import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public interface EmailService {

    boolean sendPasswordResetVerificationCode(String toEmail, String subject, String otp);

    boolean sendCourseEnrollmentSuccessEmail(String toEmail, String subject, String courseName, String startDate, String duration);

    boolean sendInstallmentPaymentSuccessEmail(String toEmail, String subject, String courseName, BigDecimal paymentAmount, String paymentDate, String paymentMethod, String nextInstallmentDate, int installmentNumber, String courseURL, String orderId);

    boolean sendUserAccountCredentialsEmail(String toEmail, String subject,String fullName, String role, String email, String password, String loginUrl);

    boolean sendGenericFileAttachment(String toEmail, String subject, String body, MultipartFile file);

}

