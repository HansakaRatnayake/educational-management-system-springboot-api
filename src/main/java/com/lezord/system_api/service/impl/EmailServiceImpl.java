package com.lezord.system_api.service.impl;

import com.lezord.system_api.config.SendGridConfig;
import com.lezord.system_api.exception.EmailServiceException;
import com.lezord.system_api.service.EmailService;
import com.lezord.system_api.util.EmailTemplateHelper;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Year;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final SendGridConfig sendGridConfig;

    private final EmailTemplateHelper emailTemplateHelper;

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Override
    public boolean sendPasswordResetVerificationCode(String toEmail, String subject, String otp) {
        logger.info("Sending password reset email to {}", toEmail);

        String htmlBody = emailTemplateHelper.loadHtmlTemplate("templates/nozomi-send-reset-password-verification-email-template.html")
                .orElseThrow(() ->  new EmailServiceException("Failed to load email template"));

        htmlBody = replaceTemplatePlaceholders(htmlBody, otp);

        Email from = new Email(sendGridConfig.getFromEmail());
        Email to = new Email(toEmail);
        Content content = new Content("text/html", htmlBody);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridConfig.getEmailKey());
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            logger.info("Email sent to {} with status code: {}", toEmail, response.getStatusCode());

            return response.getStatusCode() >= 200 && response.getStatusCode() < 300;
        } catch (IOException ex) {
            logger.error("Email sending failed: {}", ex.getMessage());
            throw new EmailServiceException("Email sending failed.");
        }
    }

    @Override
    public boolean sendCourseEnrollmentSuccessEmail(String toEmail,String subject, String courseName, String startDate, String duration) {
        logger.info("Sending course enrollment success email to {}", toEmail);

        String htmlBody = emailTemplateHelper.loadHtmlTemplate("templates/nozomi-course-enrollment-success-email-template.html")
                .orElseThrow(() -> new EmailServiceException("Failed to load course enrollment email template"));

        htmlBody = replaceCourseEnrollmentPlaceholders(htmlBody,courseName,startDate,duration);

        Email from = new Email(sendGridConfig.getFromEmail());
        Email to = new Email(toEmail);
        Content content = new Content("text/html", htmlBody);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridConfig.getEmailKey());
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            logger.info("Course enrollment email sent to {} with status code: {}", toEmail, response.getStatusCode());

            return response.getStatusCode() >= 200 && response.getStatusCode() < 300;
        } catch (IOException ex) {
            logger.error("Course enrollment email sending failed: {}", ex.getMessage());
            throw new EmailServiceException("Course enrollment email sending failed.");
        }
    }

    @Override
    public boolean sendInstallmentPaymentSuccessEmail(String toEmail, String subject, String courseName, BigDecimal paymentAmount, String paymentDate, String paymentMethod, String nextInstallmentDate, int installmentNumber, String courseURL, String orderId) {
        logger.info("Sending course installment payment success email to {}", toEmail);

        String htmlBody = emailTemplateHelper.loadHtmlTemplate("templates/nozomi-course-installment-success-email-template.html")
                .orElseThrow(() -> new EmailServiceException("Failed to load course installment payment success email template"));

        htmlBody = replaceInstallmentDetailsPlaceholders(htmlBody,courseName,paymentAmount,paymentDate,paymentMethod,nextInstallmentDate,installmentNumber,courseURL,orderId);

        Email from = new Email(sendGridConfig.getFromEmail());
        Email to = new Email(toEmail);
        Content content = new Content("text/html", htmlBody);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridConfig.getEmailKey());
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            logger.info("Course installment payment success email sent to {} with status code: {}", toEmail, response.getStatusCode());

            return response.getStatusCode() >= 200 && response.getStatusCode() < 300;
        } catch (IOException ex) {
            logger.error("Course installment payment success email sending failed: {}", ex.getMessage());
            throw new EmailServiceException("Course enrollment email sending failed.");
        }
    }

    @Override
    public boolean sendUserAccountCredentialsEmail(String toEmail, String subject, String fullName, String role, String email, String password, String loginUrl) {
        logger.info("Sending user account credentials email to {}", toEmail);

        String htmlBody = emailTemplateHelper.loadHtmlTemplate("templates/nozomi-user-account-credentials-email-template.html")
                .orElseThrow(() -> new EmailServiceException("Failed to load user account credentials email template"));

        htmlBody = replaceUserCredentialsPlaceholders(htmlBody,fullName,role,email,password,loginUrl);

        Email from = new Email(sendGridConfig.getFromEmail());
        Email to = new Email(toEmail);
        Content content = new Content("text/html", htmlBody);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridConfig.getEmailKey());
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            logger.info("User account credentials email sent to {} with status code: {}", toEmail, response.getStatusCode());

            return response.getStatusCode() >= 200 && response.getStatusCode() < 300;
        } catch (IOException ex) {
            logger.error("User account credentials email sending failed: {}", ex.getMessage());
            throw new EmailServiceException("User account credentials email sending failed.");
        }
    }

    @Override
    public boolean sendGenericFileAttachment(String toEmail, String subject, String body, MultipartFile file) {
        try {
            Email from = new Email(sendGridConfig.getFromEmail());
            Email to = new Email(toEmail);
            Content content = new Content("text/plain", body);
            Mail mail = new Mail(from, subject, to, content);

            Attachments attachment = new Attachments();
            attachment.setContent(Base64.getEncoder().encodeToString(file.getBytes()));
            attachment.setType("application/zip");
            attachment.setFilename(file.getOriginalFilename());
            attachment.setDisposition("attachment");

            mail.addAttachments(attachment);

            SendGrid sg = new SendGrid(sendGridConfig.getEmailKey());
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            return response.getStatusCode() >= 200 && response.getStatusCode() < 300;

        } catch (IOException e) {
            logger.error("Failed to send attachment email: {}", e.getMessage());
            return false;
        }
    }




    private String replaceTemplatePlaceholders(String htmlBody, String otp) {
        return htmlBody
                .replace("${otp}", otp)
                .replace("${year}", String.valueOf(Year.now().getValue()));
    }

    private String replaceCourseEnrollmentPlaceholders(String htmlBody, String courseName, String startDate, String duration) {
        return htmlBody
                .replace("${courseName}", courseName)
                .replace("${startDate}", startDate)
                .replace("${duration}", duration + " months")
                .replace("${year}", String.valueOf(Year.now().getValue()));
    }

    private String replaceInstallmentDetailsPlaceholders(String htmlBody, String courseName,BigDecimal paymentAmount, String paymentDate, String paymentMethod, String nextInstallmentDate, int installmentNumber, String courseURL, String orderId) {
        return htmlBody
                .replace("${courseName}", courseName)
                .replace("${paymentAmount}", paymentAmount + "")
                .replace("${paymentDate}", paymentDate)
                .replace("${paymentMethod}", paymentMethod)
                .replace("${installmentNumber}", installmentNumber + "")
                .replace("${nextInstallmentDate}", nextInstallmentDate)
                .replace("${courseURL}", courseURL)
                .replace("${orderId}", orderId)
                .replace("${year}", String.valueOf(Year.now().getValue()));
    }

    private String replaceUserCredentialsPlaceholders(String htmlBody, String fullName, String role, String email, String password, String loginUrl) {
        return htmlBody
                .replace("${fullName}", fullName)
                .replace("${role}", role)
                .replace("${email}", email)
                .replace("${password}", password)
                .replace("${loginUrl}", loginUrl)
                .replace("${year}", String.valueOf(Year.now().getValue()));
    }

}
