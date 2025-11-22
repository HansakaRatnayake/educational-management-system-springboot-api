package com.lezord.system_api.service;


import com.lezord.system_api.dto.request.RequestInstallmentPaymentDTO;
import com.lezord.system_api.dto.request.RequestPaymentDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface PaymentService {

    String initiatePayment(RequestPaymentDTO requestPaymentDTO);
    void initiatePaymentWithSlip(MultipartFile file, RequestPaymentDTO requestPaymentDTO);
    String initiateInstallmentPayment(RequestInstallmentPaymentDTO requestInstallmentPaymentDTO);
    boolean handlePaymentCallback(Map<String, String> payload);
    boolean handlePaymentSlipData(String paymentId);
}
