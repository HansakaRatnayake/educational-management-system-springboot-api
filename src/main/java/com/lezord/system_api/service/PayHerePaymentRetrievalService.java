package com.lezord.system_api.service;

import org.springframework.http.ResponseEntity;

public interface PayHerePaymentRetrievalService {

    ResponseEntity<String> searchPayment(String orderId);
}
