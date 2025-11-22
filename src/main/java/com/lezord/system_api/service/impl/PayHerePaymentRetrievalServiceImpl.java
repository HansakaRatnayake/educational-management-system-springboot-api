package com.lezord.system_api.service.impl;

import com.lezord.system_api.dto.response.util.ResponsePayHereRetrievalApiAccessToken;
import com.lezord.system_api.service.PayHereAccessTokenService;
import com.lezord.system_api.service.PayHerePaymentRetrievalService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class PayHerePaymentRetrievalServiceImpl implements PayHerePaymentRetrievalService {

    private final PayHereAccessTokenService payHereAccessTokenService;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String AUTH_TOKEN = "Bearer 81b76b97-fcd5-4201-a923-4eeb1f972a0a";

    @Value("${payhere.retrival_url}")
    private String payhereRetrivalUrl;

    @Override
    public ResponseEntity<String> searchPayment(String orderId) {

        ResponsePayHereRetrievalApiAccessToken retrievalApiAccessToken = payHereAccessTokenService.getRetrievalApiAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + retrievalApiAccessToken.getAccess_token());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(payhereRetrivalUrl)
                .queryParam("order_id", orderId);

        return restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                String.class
        );
    }
}
