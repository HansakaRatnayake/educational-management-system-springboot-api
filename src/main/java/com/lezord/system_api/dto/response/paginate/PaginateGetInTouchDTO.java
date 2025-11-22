package com.lezord.system_api.dto.response.paginate;


import com.lezord.system_api.dto.response.ResponseGetInTouchDTO;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
public class PaginateGetInTouchDTO {

    private long count;
    private List<ResponseGetInTouchDTO> dataList;
}
