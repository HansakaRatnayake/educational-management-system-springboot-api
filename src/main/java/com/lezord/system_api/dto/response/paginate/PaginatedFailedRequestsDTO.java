package com.lezord.system_api.dto.response.paginate;

import com.lezord.system_api.dto.response.ResponseFailedRequestCustomDTO;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PaginatedFailedRequestsDTO {
    private long count;
    private List<ResponseFailedRequestCustomDTO> dataList;
}
