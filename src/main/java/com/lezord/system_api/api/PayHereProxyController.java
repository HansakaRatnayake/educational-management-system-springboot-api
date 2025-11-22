package com.lezord.system_api.api;

import com.lezord.system_api.service.PayHerePaymentRetrievalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payhere")
public class PayHereProxyController {

    private final PayHerePaymentRetrievalService payHereService;


    @GetMapping("/search")
    public ResponseEntity<String> searchPayment(@RequestParam String orderId) {
        return payHereService.searchPayment(orderId);
    }
}
