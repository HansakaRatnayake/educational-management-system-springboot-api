package com.lezord.system_api.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseFAQDTO {
    private String faqId;
    private String question;
    private String answer;
    private boolean activeStatus;
    private int orderId;
}
