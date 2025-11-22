package com.lezord.system_api.dto.response.paginate;


import com.lezord.system_api.dto.response.ResponseClientViewInstructorDetailDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginateClientViewInstructorDetailDTO {
    private Long count;
    private List<ResponseClientViewInstructorDetailDTO> dataList;

}
