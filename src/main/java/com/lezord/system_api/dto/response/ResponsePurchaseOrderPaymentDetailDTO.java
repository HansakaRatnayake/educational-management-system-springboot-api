package com.lezord.system_api.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePurchaseOrderPaymentDetailDTO {

    private String paidAt;
    private boolean installmentEnabled;
    private String method;
    private String cardNumber;
    private String cardHolderName;
    private String cardExpiryDate;
    private String orderId;
    private String status;
    private List<ResponseStudentInstallmentPlanDTO> studentInstallmentPlanDTO;
}
