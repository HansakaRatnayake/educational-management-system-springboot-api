package com.lezord.system_api.service.impl;

import com.lezord.system_api.dto.response.util.ResponsePayHereRetrievalApiAccessToken;
import com.lezord.system_api.service.PayHereAccessTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@Service
public class PayHereAccessTokenServiceImpl implements PayHereAccessTokenService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${payhere.retrival.token.url}")
    private String payhereRetrivalTokenUrl;

    @Value("${payhere.retrival.api.key}")
    private String payhereRetrivalApiKey;

    @Value("${payhere.retrival.api.keyPrefix}")
    private String payhereRetrivalApiKeyPrefix;

    @Override
    public ResponsePayHereRetrievalApiAccessToken getRetrievalApiAccessToken() {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", payhereRetrivalApiKeyPrefix + payhereRetrivalApiKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<ResponsePayHereRetrievalApiAccessToken> response = restTemplate.exchange(
                payhereRetrivalTokenUrl,
                HttpMethod.POST,
                entity,
                ResponsePayHereRetrievalApiAccessToken.class
        );
        return response.getBody();

    }
}
