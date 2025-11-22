package com.lezord.system_api.dto.response.paginate;

import com.lezord.system_api.dto.response.ResponseSuccessStoryDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginateSuccessStoryDTO {

    private Long count;
    private List<ResponseSuccessStoryDTO> dataList;
}
