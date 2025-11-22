package com.lezord.system_api.service;

import com.lezord.system_api.dto.response.ResponsePurchaseDetailDTO;
import com.lezord.system_api.dto.response.paginate.PaginatePurchaseDetailDTO;

import java.util.List;

public interface PurchaseDetailService {

    List<ResponsePurchaseDetailDTO> getPurchaseDetails(String studentId);
    PaginatePurchaseDetailDTO getAllPendingSlipVerifiedData(String searchText, int page, int size);
}
