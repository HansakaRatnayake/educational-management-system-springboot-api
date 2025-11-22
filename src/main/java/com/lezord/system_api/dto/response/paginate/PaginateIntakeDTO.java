package com.lezord.system_api.dto.response.paginate;

import com.lezord.system_api.dto.response.ResponseIntakeDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginateIntakeDTO {

    private Long count;
    private List<ResponseIntakeDTO> dataList;
}
