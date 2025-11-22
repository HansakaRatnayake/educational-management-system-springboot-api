package com.lezord.system_api.dto.response.util;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePayHereRetrievalApiAccessToken {

    private String access_token;
    private String token_type;
    private String expires_in;
    private String scope;

}
