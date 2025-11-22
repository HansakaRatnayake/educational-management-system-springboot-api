package com.lezord.system_api.dto.response.paginate;

import com.lezord.system_api.dto.response.ResponsePurchaseDetailDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatePurchaseDetailDTO {

    private Long count;
    private List<ResponsePurchaseDetailDTO> dataList;
}
