package com.lezord.system_api.dto.response.paginate;

import com.lezord.system_api.dto.response.ResponseApplicationUserDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginateApplicationUserDTO {
    private Long count;
    private List<ResponseApplicationUserDTO> dataList;
}
