package com.lezord.system_api.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lezord.system_api.dto.request.RequestInstallmentPaymentDTO;
import com.lezord.system_api.dto.request.RequestPaymentDTO;
import com.lezord.system_api.service.PaymentService;
import com.lezord.system_api.util.StandardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @PostMapping(value = "/initiate-payment", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> directToPayHereGatewayServer(
            @RequestBody RequestPaymentDTO dto
    ) {
        String htmlForm = paymentService.initiatePayment(dto); // this returns HTML string
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.TEXT_HTML)
                .body(htmlForm);
    }

    @PostMapping(value = "/initiate-payment-with-slip")
    public ResponseEntity<StandardResponseDTO> directToNozomiServer(
            @RequestParam("data") String data,
            @RequestParam("slip") MultipartFile slip
    ) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        RequestPaymentDTO dt = objectMapper.readValue(data, RequestPaymentDTO.class);

        paymentService.initiatePaymentWithSlip(slip, dt); // this returns HTML string
        return ResponseEntity
                .status(201).body(StandardResponseDTO.builder()
                        .code(201)
                        .message("Payment processed")
                        .data(null)
                        .build());
    }

    @PostMapping(value = "/initiate-installment-payment", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> directToPayHereGatewayServerForInstallmentPayment(
            @RequestBody RequestInstallmentPaymentDTO dto
    ) {
        String htmlForm = paymentService.initiateInstallmentPayment(dto); // this returns HTML string
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.TEXT_HTML)
                .body(htmlForm);
    }


    @PostMapping("/notify")
    public ResponseEntity<StandardResponseDTO> gatewayCallBack(@RequestParam Map<String, String> payload) {
        boolean updated = paymentService.handlePaymentCallback(payload);
        return ResponseEntity
                .status(updated ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(
                        StandardResponseDTO.builder()
                                .code(updated ? 200 : 400)
                                .message(updated ? "Payment processed" : "Invalid order ID or already processed")
                                .data(null)
                                .build()
                );
    }

    @PostMapping("/verify-payment")
    public ResponseEntity<StandardResponseDTO> handlePaymentSlipData(@RequestParam String paymentId) {
        boolean updated = paymentService.handlePaymentSlipData(paymentId);
        return ResponseEntity
                .status(updated ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(
                        StandardResponseDTO.builder()
                                .code(updated ? 200 : 400)
                                .message(updated ? "Payment processed" : "Invalid order ID or already processed")
                                .data(null)
                                .build()
                );
    }

}
