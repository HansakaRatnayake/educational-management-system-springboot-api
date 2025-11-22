package com.lezord.system_api.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePurchaseDetailDTO {

    private ResponsePurchaseCourseDetailDTO courseDetailDTO;
    private ResponsePurchaseOrderPaymentDetailDTO orderPaymentDetailDTO;
    private ResponseStudentDTO responseStudentDTO;
    private String paymentSlip;

}
